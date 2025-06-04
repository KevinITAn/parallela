import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Earthquakes {
    private record Coordinate(double lat, double lon) {
        /**
         * Returns the distance (expressed in km) between two coordinates
         *
         * @param to
         * @return Returns the distance expressed in km
         */
        public double distance(final Coordinate to) {
            final double earthRadius = 6371.000; // km
            final double dLat = Math.toRadians(to.lat - this.lat);
            final double dLng = Math.toRadians(to.lon - this.lon);
            final double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(to.lat))
                    * Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2.0) * Math.sin(dLng / 2.0);
            final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return (earthRadius * c);
        }

        @Override
        public String toString() {
            return String.format("[%.5f, %.5f]", lat, lon);
        }
    }

    private record Earthquake(Date time, Coordinate position, double depth, double magnitude, String place) {
        private final static String CSV_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

        public static Optional<Earthquake> parse(final String csvLine) {
            final String[] splits = csvLine.split(CSV_REGEX);
            if (splits.length != 15) {
                System.err.println("Failed to parse: " + csvLine);
                return Optional.empty();
            }

            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            final Date time;
            try {
                time = SDF.parse(splits[0]);
            } catch (final ParseException e) {
                return Optional.empty();
            }

            final Optional<Double> lat = tryParseDouble(splits[1]);
            final Optional<Double> lon = tryParseDouble(splits[2]);
            final Optional<Double> depth = tryParseDouble(splits[3]);
            final Optional<Double> mag = tryParseDouble(splits[4]);
            if (Stream.of(lat, lon, depth, mag).allMatch(Optional::isPresent)) {
                final String place = splits[13];
                return Optional.of(new Earthquake(time, new Coordinate(lat.get(), lon.get()), depth.get(), mag.get(), place));
            }
            System.err.println("Error parsing (lat, lon, depth, meg): " + csvLine);
            return Optional.empty();
        }

        private static Optional<Double> tryParseDouble(final String str) {
            try {
                return Optional.of(Double.parseDouble(str));
            } catch (final NumberFormatException e) {
                return Optional.empty();
            }
        }

        @Override
        public String toString() {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(time) + " mag: " + magnitude
                    + " depth: " + depth + "km @ " + position + " " + place;
        }
    }

    private static List<Earthquake> loadEarthquakeDB(final Path filePath) {
        try {
            final List<String> readAllLines = Files.readAllLines(filePath);
            return readAllLines.stream()
                    .skip(1) // skip header line
                    .map(Earthquake::parse)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (final IOException ioe) {
            final String msg = String.format("Failed to parse '%s'. %s", filePath.toAbsolutePath(), ioe.getMessage());
            throw new RuntimeException(msg);
        }
    }

    public static void main(final String[] args) {
        final Path filePath = Paths.get("src\\2014-2015.csv");

        if (!filePath.toFile().exists()) {
            System.err.println("File not found! " + filePath.toAbsolutePath());
            return;
        }

        final long startTime = System.currentTimeMillis();
        final List<Earthquake> quakes = loadEarthquakeDB(filePath);
        final long computeTime = System.currentTimeMillis();

        if (quakes.isEmpty()) {
            System.out.println("No earthquakes found!");
            return;
        }
        System.out.println("Loaded " + quakes.size() + " earthquakes");

        final Coordinate location = new Coordinate(46.012013, 8.961072);
        System.out.println("Searching for nearest earthquake ...");
        Earthquake curNearestQuake = null;
        double curNearestDistance = Double.MAX_VALUE;
        for (final Earthquake quake : quakes) {
            final double distance = quake.position().distance(location);
            if (curNearestDistance > distance) {
                curNearestDistance = distance;
                curNearestQuake = quake;
            }
        }

        //the most distant earthquake
        Earthquake mostDistantQuake=quakes.parallelStream()
                .max(Comparator.comparing(eq->eq.position().distance(location)))
                .orElse(null);
        //the strongest earthquake
        Earthquake strongestQuake=quakes.parallelStream()
                .max(Comparator.comparing(Earthquake::magnitude))
                .orElse(null);
        //the 10 closest earthquakes of magnitude between 4 and 6, but at a distance of at least 2000 km
        List<Earthquake> tenClosestQuake= quakes.parallelStream()
                .filter(earthquake -> earthquake.magnitude() >= 4 && earthquake.magnitude() <= 6 && earthquake.position.distance(location) >= 2000)
                .sorted(Comparator.comparing(eq->eq.position().distance(location)))
                .limit(10)
                .toList();
        //the number of earthquakes with latitude 46 (46.0 <= latitude < 47.0)
        long nQuakeLatitude46= quakes.parallelStream()
                .filter(eq->eq.position().lat() >= 46 && eq.position().lat() < 47)
                .count();
        //the number of earthquakes with longitude 8 (8.0 <= longitude < 9.0)
        long nQuakeLongitude8= quakes.parallelStream()
                .filter(eq->eq.position().lon() >= 8 && eq.position().lon() < 9)
                .count();
        //the number of earthquakes for depth bands of 100 km (number between [0, 100), between [100,200), etc.)
        Map<Integer, Long> nQuakeForBand100 = quakes.parallelStream()
                .collect(Collectors.groupingBy(
                        eq -> (int)(eq.depth() / 100),  // calcola la banda: 0 => [0,100), 1 => [100,200), ecc.
                        Collectors.counting()          // conta quante volte si verifica quella banda
                ));
        //the number of earthquakes per intensity bands (Number between [0, 1), between [1,2), etc.)
        Map<Integer,Long> nQuakeIntensity= quakes.parallelStream()
                .collect(Collectors.groupingBy(
                        eq->(int)(eq.magnitude()/10),
                        Collectors.counting()
                ));


        final long endTime = System.currentTimeMillis();
        System.out.println("Completed in " + ((endTime - startTime)) + " ms" + " (computation time=" + (endTime - computeTime) + " ms)");
        // Results
        System.out.println("Nearest  : " + curNearestQuake + " distance: " + curNearestDistance);

        System.out.println("The most distant earthquake:"+mostDistantQuake);
        System.out.println("The strongest earthquake"+strongestQuake);
        System.out.println("the 10 closest earthquakes of magnitude between 4 and 6, but at a distance of at least 2000 km: "+tenClosestQuake);
        System.out.println("The number of earthquakes with latitude 46 (46.0 <= latitude < 47.0)"+nQuakeLatitude46);
        System.out.println("The number of earthquakes with longitude 8 (8.0 <= longitude < 9.0)"+nQuakeLongitude8);
        System.out.println("the number of earthquakes for depth bands of 100 km (number between [0, 100), between [100,200), etc.):"+nQuakeForBand100);
        System.out.println("the number of earthquakes per intensity bands (Number between [0, 1), between [1,2), etc.):"+nQuakeIntensity);
    }
}
