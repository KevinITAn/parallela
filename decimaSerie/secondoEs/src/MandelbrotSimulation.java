import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This JPanel holds the image
 */
class ImagePanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -765326845521113343L;

    // contains the image that is computed by this program
    private final Lock imageLock = new ReentrantLock();
    private final BufferedImage image;
    private final JPanel imagePanel;

    public ImagePanel(final int w, final int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        paintGray();

        // imagePanel is a JPanel that draws the image data
        imagePanel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 4002004872041961024L;

            @Override
            protected void paintComponent(final Graphics g) {
                // Multiple access to update image!
                imageLock.lock();
                try {
                    g.drawImage(image, 0, 0, null);
                } finally {
                    imageLock.unlock();
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(w, h));
        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
    }

    /**
     * Adds the give rowData to the image and updates the image
     */
    public void setRowAndUpdate(final int[] rowData, final int row) {
        final int width = getWidth();

        // Image is a shared resource!
        imageLock.lock();
        try {
            image.setRGB(0, row, width, 1, rowData, 0, width);
        } finally {
            imageLock.unlock();
        }
        // Repaint just the newly computed row.
        imagePanel.repaint(0, row, width, 1);
    }

    private void paintGray() {
        final Graphics g = image.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    public void resetImage() {
        paintGray();
        imagePanel.repaint();
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }
}

/**
 * Mandelbrot generator class
 */
class Mandelbrot {
    private final int[] palette = new int[256];
    private static final double X_MIN = -1.6744096740931858;
    private static final double X_MAX = -1.6744096740934730;
    private static final double Y_MIN = 4.716540768697223E-5;
    private static final double Y_MAX = 4.716540790246652E-5;
    private static final int maxIterations = 10000;

    private final int width;
    private final int height;
    private final double dx;
    private final double dy;

    public Mandelbrot(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.dx = (X_MAX - X_MIN) / (width - 1);
        this.dy = (Y_MAX - Y_MIN) / (height - 1);

        for (int i = 0; i < 256; i++)
            palette[i] = Color.getHSBColor(i / 255F, 1, 1).getRGB();
    }

    /**
     * Returns the imageData for given row
     */
    public int[] computeRow(final int row) {
        final int[] rgbRow = new int[width];
        final double y = Y_MAX - dy * row;
        for (int col = 0; col < width; col++) {
            final double x = X_MIN + dx * col;
            final int count = computePoint(x, y);
            if (count == maxIterations) {
                rgbRow[col] = 0;
            } else {
                rgbRow[col] = palette[count % palette.length];
            }
        }
        return rgbRow;
    }

    private int computePoint(final double x, final double y) {
        int count = 0;
        double xx = x;
        double yy = y;
        while (count < maxIterations && (xx * xx + yy * yy) < 4) {
            count++;
            final double newxx = xx * xx - yy * yy + x;
            yy = 2 * xx * yy + y;
            xx = newxx;
        }
        return count;
    }
}

public class MandelbrotSimulation extends JPanel {
    @Serial
    private static final long serialVersionUID = -765326845524613343L;

    // the threads that compute the image
    //private Thread[] workers;

    private ExecutorService executor;


    // used to signal the thread to abort && button
    private volatile boolean running;

    // button the user can click to start or abort the thread
    private final JButton startButton;

    // for specifying the number of threads to be used
    private final JComboBox<Integer> threadCountSelect;

    private final ImagePanel imagePanel;

    private final Mandelbrot fractal;

    public MandelbrotSimulation() {

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setLayout(new BorderLayout());

        // Top - Label, ComboBox and Button
        final JPanel topPanel = new JPanel();
        startButton = new JButton("Start");
        topPanel.add(startButton);
        threadCountSelect = new JComboBox<>();
        for (int i = 1; i <= 32; i++)
            threadCountSelect.addItem(i);

        topPanel.add(new JLabel("Number of threads to use: "));
        topPanel.add(threadCountSelect);

        topPanel.setBackground(Color.LIGHT_GRAY);
        add(topPanel, BorderLayout.NORTH);
        startButton.addActionListener(actionEvent -> {
            if (running) {
                stop();
            } else {
                start();
            }
        });

        // Main - Image Panel
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = screenSize.width - 200;
        final int height = screenSize.height - 200;
        imagePanel = new ImagePanel(width, height);
        add(imagePanel, BorderLayout.CENTER);
        fractal = new Mandelbrot(width, height);
    }

    /**
     * This method is called when the user clicks the Start button, while no
     * computation is in progress. It starts as many new threads as the user has
     * specified, and assigns a different part of the image to each thread. The
     * threads are run at lower priority than the event-handling thread, in order to
     * keep the GUI responsive.
     */
    void start() {
        startButton.setText("Abort");
        startButton.setEnabled(true);
        imagePanel.resetImage();
        threadCountSelect.setEnabled(false);

        final int taskCount;
        if (threadCountSelect.getSelectedItem() instanceof Integer selectedItemIntValue) {
            taskCount = selectedItemIntValue;
        } else {
            System.err.println("Invalid or null thread count selected.");
            taskCount=1;
        }

        final int height = imagePanel.getHeight();
        final double rowsPerThread = height * 1.0 / taskCount;
        running = true;

        executor = Executors.newWorkStealingPool(taskCount);

        // Contatore atomico per sapere quando tutti i task sono finiti
        AtomicInteger finishedCount = new AtomicInteger(0);

        for (int i = 0; i < taskCount; i++) {
            final int startRow = (int) (rowsPerThread * i);
            final int endRow = (i == taskCount - 1) ? height - 1 : (int) (rowsPerThread * (i + 1)) - 1;

            executor.submit(() -> {
                for (int row = startRow; row <= endRow; row++) {
                    if (!running || Thread.currentThread().isInterrupted()) break;
                    final int[] rgbRow = fractal.computeRow(row);
                    imagePanel.setRowAndUpdate(rgbRow, row);
                }

                if (finishedCount.incrementAndGet() == taskCount) {
                    SwingUtilities.invokeLater(() -> {
                        running = false;
                        startButton.setText("Start");
                        startButton.setEnabled(true);
                        threadCountSelect.setEnabled(true);
                    });
                    executor.shutdown();
                }
            });
        }
    }


    /**
     * Called when the user clicks the button while a thread is running. A signal is
     * sent to the thread to terminate, by setting the value of the signaling
     * variable, running, to false.
     */
    void stop() {
        running = false;
        executor.shutdownNow();//blocca tutti i task
        startButton.setEnabled(false);
    }

    /**
     * Program starting point
     */
    public static void main(final String[] args) {
        final JFrame window = new JFrame("Mandelbrot Simulation (Threads)");
        final MandelbrotSimulation content = new MandelbrotSimulation();
        window.setContentPane(content);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setResizable(false);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((screenSize.width - window.getWidth()) / 2, (screenSize.height - window.getHeight()) / 2);
        window.setVisible(true);
    }
}
