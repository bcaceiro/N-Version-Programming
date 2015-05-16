package client.Voter;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by jorl17 on 15/05/15.
 */
public class Voter {
    private String[] urls;
    private int maxTime;
    private InsulineService factory;

    public Voter(String[] urls, int maxTime, InsulineService factory) {
        this.urls = urls;
        this.maxTime = maxTime;
        this.factory = factory;
    }

    public Voter(InsulineService factory) {
        this ( new String[] { "http://qcs06.dei.uc.pt:8080/insulin?wsdl",
                        "http://vm-sgd17.dei.uc.pt:80/InsulinDoseCalculator?wsdl",
                        "http://qcs01.dei.uc.pt:8080/InsulinDoseCalculator?wsdl",
                        "http://qcs12.dei.uc.pt:8080/insulin?wsdl",
                        "http://qcs07.dei.uc.pt:8080/insulin?wsdl",
                        "http://qcs10.dei.uc.pt:8080/insulin?wsdl",
                        "http://liis-lab.dei.uc.pt:8080/Server?wsdl"},
                3500, factory);
    }

    private int[] invokeServices() {
        ExecutorService executorService = Executors.newFixedThreadPool(urls.length);
        Set<Callable<ResultPair>> callables = new HashSet<>();

        for (String url : urls)
            callables.add(factory.constructNew().setUrl(url));

        try {
            List<Future<ResultPair>> futures;
            String[] newUrls = new String[urls.length];
            futures = executorService.invokeAll(callables, maxTime, TimeUnit.MILLISECONDS);
            int[] results = new int[futures.size()];
            for ( int i = 0; i < futures.size(); i++) {
                try {
                    results[i] = futures.get(i).isCancelled() ? futures.get(i).get().result : -1;
                    newUrls[i] = futures.get(i).get().url;
                } catch (Exception e) {
                    results[i] = -1;
                    newUrls[i] = null;
                }
            }

            fixUrls(newUrls, urls);
            urls = newUrls;

            return results;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int[] results = new int[urls.length];
        for (int i = 0;i < results.length; i++) results[i] = -1;
        return results;
    }

    // Depois de escrever isto, só me resta o suicídio -- Maxi.
    private boolean existsInArray(String[] array, String elem) {
        for ( String e : array )
            if ( e!=null && e.equals(elem) )
                return true;
        return false;
    }

    private void fixUrls(String[] newUrls, String[] urls) {
        ArrayList<String> missingUrls = new ArrayList<>();
        for (String url : urls) {
            if (!existsInArray(newUrls, url))
                missingUrls.add(url);
        }

        int c = 0;
        for (int i = 0; i < newUrls.length; i++)
            if ( newUrls[i] == null )
                newUrls[i] = missingUrls.get(c++);

    }

    private int[] getMajority(int[] results) {
        int prev = results[0];
        int pop = results[0];
        int count = 1;
        int maxCount = 1;

        for (int i = 1; i < results.length; i++) {
            if (results[i] == prev)
                count++;
            else {
                if (count > maxCount) {
                    pop = results[i-1];
                    maxCount = count;
                }
                prev = results[i];
                count = 1;
            }
        }
        int[] res = new int[2];
        if (count > maxCount) {
            res[0] = results[results.length-1];
            res[1] = count;
        } else {
            res[0] = pop;
            res[1] = maxCount;
        }

        return res;
    }

    private int countInvalid(int[] results, int i) {
        int count = 0;
        for ( int r : results )
            if ( r == -1 )
                count++;
            else
                break;

        return count;
    }

    private int findMinExcludeInvalid(int[] results) {
        int i;
        for ( i = 0; i < results.length && results[i] == -1; i++ ) ;
        return results[i == 0 ? i : i-1];
    }

    private ArrayList<Integer> findEquivalent(int[] results, int val) {
        ArrayList<Integer> equiv = new ArrayList<Integer>();
        for ( int i = 0; i < results.length && results[i] <= val+1; i++)
            if ( results[i] != -1 && ( (results[i] - 1 == val) || (results[i] + 1 == val) || (results[i] == val)))
                equiv.add(results[i]);

        return equiv;
    }

    public ArrayList<Integer> findMajorityRangeWithEquivalence(int[] results) {
        // results is sorted

        int min = findMinExcludeInvalid(results), max = results[results.length-1];

        ArrayList<Integer> best = findEquivalent(results, min);
        for (int i = min+1; i <= max; i++) {
            ArrayList<Integer> thisCount = findEquivalent(results, i);
            if (thisCount.size() > best.size()) {
                best = thisCount;
            }
        }

        return best;
    }

    public int median(ArrayList<Integer> results) {
        if (results.size() % 2 == 0)
            return (results.get(results.size()/2) + results.get(results.size()/2 - 1))/2;
        else
            return results.get(results.size()/2);
    }



    public VoterResults vote() {
        long targetTime = System.nanoTime() + maxTime * 1000000L;
        int[] resultsOrig;
        do {
            System.out.println("Voting!");
            int[] results = invokeServices();/*{ 1, 1, 1, 7, 3, 3, 3, 9, 9, 9, 9, 9};*/
            resultsOrig = new int[results.length];
            System.arraycopy(results, 0, resultsOrig, 0, results.length);
            Arrays.sort(results);
            int[] tmp = getMajority(results);
            int major = tmp[0], count = tmp[1];
            if (count >= results.length / 2.0 && major != -1) {
                return new VoterResults("majority", major, count, resultsOrig, urls);
            } else {
                ArrayList<Integer> majorityRange = findMajorityRangeWithEquivalence(results);
                //System.out.println("Majority range: ");
                //System.out.println(majorityRange);
                if ( majorityRange.size() > results.length / 2.0 )
                    return new VoterResults("median", median(majorityRange), -1, resultsOrig, urls);
            }
        } while ( System.nanoTime() < targetTime );

        return new VoterResults("Timeout", -1, -1, resultsOrig, urls);
    }

    public static void main(String[] args) {
        InsulineServiceMealtimeInsulinDose t = new InsulineServiceMealtimeInsulinDose(50, 50, 50, 50, 50);
        Voter m = new Voter(t);
        VoterResults results = m.vote();
        if (results.success()) {
            if ( results.median() )
                System.out.println("Majority result (median): " + results.majority + "");
            else
                System.out.println("Majority result: " + results.majority + " (" + results.count + " votes)");
            for (int r : results.results )
                System.out.println(r);
        } else {
            System.out.println(results.reason);
        }
    }
}
