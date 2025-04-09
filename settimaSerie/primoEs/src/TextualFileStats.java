import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TextualFileStats {
    private final ConcurrentMap<Integer, Long> wordLengthCounts = new ConcurrentHashMap<>();
    //add functionality
    private final ConcurrentMap<Character, String> longestWordByStartChar = new ConcurrentHashMap<>();
    private final ConcurrentMap<Character,WordStats> averageWordStats  = new ConcurrentHashMap<>();

    private void readAndProcessFile(Path filePath) {
        try {
            for (final String line : Files.readAllLines(filePath)) {
                for (String w : line.split("\\s+")) {
                    // Cleanup word by removing leading/tailing white spaces or non-alphanumeric characters
                    final String word = w.trim().replaceAll("^\\W+|\\W+$", "");
                    if (word.isEmpty()) {
                        continue;
                    }
                    final int length = word.length();
                    final char firstChar = word.charAt(0);

                    wordLengthCounts.compute(length, (key, value) -> value == null ? 1L : value + 1);
                    longestWordByStartChar.compute(firstChar,(key,value)-> value==null || word.length()>value.length() ? word : value);
                    averageWordStats.compute(firstChar, (key, existingStats) -> {
                        WordStats newStats = new WordStats(word.length(), 1);
                        return (existingStats == null) ? newStats : existingStats.merge(newStats);
                    });
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath);
        }
    }

    private void computeStats() {
        int statsCount = 1;
        while (!Thread.currentThread().isInterrupted()) {
            final StringBuilder sb = new StringBuilder();
            synchronized (wordLengthCounts) {
                wordLengthCounts.forEach((k, v) -> sb.append(String.format("(%s: %s)", k, v)));
            }
            System.out.println("Stats " + statsCount++ + ": " + sb);

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while computing stats");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    void process(List<Path> filePaths) {
        // Create and start threads for each file
        final List<Thread> threads = new ArrayList<>();
        for (Path filePath : filePaths) {
            threads.add(new Thread(() -> readAndProcessFile(filePath)));
        }
        // Create stats thread
        final Thread statsThreads = new Thread(this::computeStats);

        // Start threads
        threads.forEach(Thread::start);
        statsThreads.start();

        try {
            // Wait for all worker threads to finish
            for (Thread thread : threads) {
                thread.join();
            }

            // Once processing threads have completed, interrupt and wait for the stats thread
            statsThreads.interrupt();
            statsThreads.join();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for threads to terminate");
        }

        // Print the results
        final StringBuilder sb = new StringBuilder();
        wordLengthCounts.forEach((k, v) -> sb.append(String.format("(%s: %s)", k, v)));
        System.out.println("Final stats : " + sb);
    }

    public static void main(String[] args) {
        // TODO: update rootFolder
        final Path rootFolder = Path.of("C:\\Users\\kevin\\OneDrive\\Documenti\\GitHub\\parallela\\settimoEs\\primoEs\\src");
        new TextualFileStats().process(List.of(
                rootFolder.resolve("pride-and-prejudice.txt"),
                rootFolder.resolve("moby-dick.txt"),
                rootFolder.resolve("dorian-gray.txt"),
                rootFolder.resolve("romeo-and-juliet.txt")
        ));
    }
}
