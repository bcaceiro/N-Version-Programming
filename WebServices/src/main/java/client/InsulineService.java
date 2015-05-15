package client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jorl17 on 15/05/15.
 */
public class InsulineService {
    // FIXME: use the timeouts and shit for something
    private InsulinDoseCalculator service = null;
    private int numTries, maxTime;
    private String url;
    public InsulineService(String url, int numTries, int maxTime) {
        this.url = url;
        this.numTries = numTries;
        this.maxTime = maxTime;
    }

    public void connect() {
        try {
            service = new InsulinDoseCalculatorService(new URL(url)).getInsulinDoseCalculatorPort();
            //service = new InsulinDoseCalculatorService(new URL("http://qcs12.dei.uc.pt:8080/insulin?wsdl")).getInsulinDoseCalculatorPort();
            //service = new InsulinDoseCalculatorService(new URL("http://qcs18.dei.uc.pt:8080/insulin?wsdl")).getInsulinDoseCalculatorPort();
            //service = new InsulinDoseCalculatorService(new URL("http://localhost:9000/InsulinDoseCalculator?wsdl")).getInsulinDoseCalculatorPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
