package client.Voter;

/**
 * Created by jorl17 on 15/05/15.
 */
public class VoterResults {
    int majority;
    int count;
    int[] results;
    String[] urls;
    String reasonForFailure;

    public VoterResults(int majority, int count, int[] results, String[] urls) {
        this.majority = majority;
        this.count = count;
        this.results = results;
        this.urls = urls;
    }

    public VoterResults(String reasonForFailure) {
        this.reasonForFailure = reasonForFailure;
    }

    // Found by Median
    public VoterResults(int major, int[] results, String[] urls) {
        this(major, -1, results, urls);
    }

    public boolean success() {
        return reasonForFailure == null;
    }

    public boolean median() { return count == -1; }
}
