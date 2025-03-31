final class Coordinate {
    private final double lat;
    private final double lon;

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Returns the distance (expressed in km) between two coordinates
     */
    public double distance(final Coordinate from) {
        final double dLat = Math.toRadians(from.lat - this.lat);
        final double dLng = Math.toRadians(from.lon - this.lon);
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(from.lat))
                * Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        return (6371.000 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    @Override
    public String toString() {
        return "[" + lat + ", " + lon + "]";
    }
}
