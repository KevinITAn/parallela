import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serial;
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
    private static long startTime=0;
    @Serial
    private static final long serialVersionUID = -765326845524613343L;

    // the threads that compute the image
    private Thread[] workers;

    private final Lock lock = new ReentrantLock();

    // used to signal the thread to abort
    private volatile boolean running;

    // how many threads have finished running?
    private int threadsCompleted;
    private int threadsCount;


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
        startTime = System.currentTimeMillis();
        // change name while computation is in progress
        startButton.setText("Abort");
        imagePanel.resetImage();

        // will be re-enabled when all threads finish
        threadCountSelect.setEnabled(false);

        final int threadCount;
        if (threadCountSelect.getSelectedItem() instanceof Integer selectedItemIntValue) {
            threadCount = selectedItemIntValue;
        } else {
            // Handle error or fallback case
            System.err.println("Invalid or null thread count selected.");
            threadCount = 1;
        }
        workers = new Thread[threadCount];

        // How many rows of pixels should each thread compute?
        double rowsPerThread;

        final int height = imagePanel.getHeight();

        rowsPerThread = height * 1. / threadCount;

        // Set the signal before starting the threads!
        running = true;
        // Records how many of the threads have terminated.
        lock.lock();
        try {
            threadsCompleted = 0;
            threadsCount = threadCount;
        } finally {
            lock.unlock();
        }
        for (int i = 0; i < threadCount; i++) {
            final int startRow; // first row computed by thread number i
            final int endRow; // last row computed by thread number i
            // Create and start a thread to compute the rows of the image from
            // startRow to endRow. Note that we have to make sure that
            // the endRow for the last thread is the bottom row of the image.
            startRow = (int) (rowsPerThread * i);
            if (i == threadCount - 1) {
                endRow = height - 1;
            } else {
                endRow = (int) (rowsPerThread * (i + 1)) - 1;
            }

            workers[i] = new Thread(() -> {
                try {
                    // Compute one row of pixels.
                    for (int row = startRow; row <= endRow; row++) {
                        final int[] rgbRow = fractal.computeRow(row);
                        // Check for the signal to abort the computation.
                        if (!running) {
                            return;
                        }
                        imagePanel.setRowAndUpdate(rgbRow, row);
                    }
                } finally {
                    // make sure this is called when the thread finishes for
                    // any reason.
                    threadFinished();
                }
            });
        }

        for (int i = 0; i < threadCount; i++)
            workers[i].start();

    }

    /**
     * Called when the user clicks the button while a thread is running. A signal is
     * sent to the thread to terminate, by setting the value of the signaling
     * variable, running, to false.
     */
    void stop() {
        // will be re-enabled when all threads finish
        startButton.setEnabled(false);
        running = false;
    }

    /**
     * Called by each thread upon completing its work
     */
    void threadFinished() {
        lock.lock();
        try {
            threadsCompleted++;
            if (threadsCompleted == threadsCount) {
                // all threads have finished
                startButton.setText("Start");
                startButton.setEnabled(true);
                // Make sure running is false after the thread ends.
                running = false;

                workers = null;
                threadCountSelect.setEnabled(true); // re-enable pop-up menu
                imagePanel.repaint();
                System.out.println("Time:" + (startTime-System.currentTimeMillis()));

            }
        } finally {
            lock.unlock();
        }
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
