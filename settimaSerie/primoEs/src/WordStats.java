/**
 * Represents word statistics with total length and count of words.
 */
public record WordStats(long totalLength, long count) {

    /**
     * Calculates the average length of words.
     *
     * @return the average word length as a float.
     */
    float average() {
        return (count == 0) ? 0 : (float) totalLength / count;
    }

    /**
     * Merges this WordStats with another WordStats.
     *
     * @param other The other WordStats to merge with.
     * @return A new WordStats instance with combined values.
     */
    public WordStats merge(WordStats other) {
        return new WordStats(totalLength + other.totalLength, count + other.count);
    }

    /**
     * Returns a string representation of this WordStats.
     *
     * @return A string detailing total length, count, and average of words.
     */
    @Override
    public String toString() {
        return "totalLength=" + totalLength + ", count=" + count + ", average=" + average();
    }
}
