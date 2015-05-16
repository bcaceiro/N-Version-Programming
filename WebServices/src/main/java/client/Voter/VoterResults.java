package client.Voter;

import java.util.Arrays;

/**
 * Created by jorl17 on 15/05/15.
 */
public class VoterResults {
    int majority;
    int count;
    int[] results;
    String[] urls;
    String reason;

    public VoterResults(String reason, int majority, int count, int[] results, String[] urls) {
        this.majority = majority;
        this.count = count;
        this.results = results;
        this.urls = urls;
        this.reason = reason;
    }

    public boolean success() {
        return majority != -1;
    }

    public boolean median() { return count == -1; }

    @Override
    public String toString() {
        return "VoterResults{" +
                "majority=" + majority +
                ", count=" + count +
                ", results=" + Arrays.toString(results) +
                ", urls=" + Arrays.toString(urls) +
                ", reason='" + reason + '\'' +
                '}';
    }
}
