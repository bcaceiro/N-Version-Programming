package client.Voter;

import client.InsulinDoseCalculator;
import client.InsulinDoseCalculatorService;
import com.sun.xml.ws.client.BindingProviderProperties;

import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by jorl17 on 15/05/15.
 */
public abstract class InsulineService implements Callable<Integer> {
    protected InsulinDoseCalculator service = null;
    private String url;
    public InsulineService(String url) {
        this.url = url;
    }

    private void connect() {
        try {
            InsulinDoseCalculatorService insulinDoseCalculatorService = new InsulinDoseCalculatorService(new URL(url));
            service = insulinDoseCalculatorService.getInsulinDoseCalculatorPort();
            //service = new InsulinDoseCalculatorService(new URL("http://qcs12.dei.uc.pt:8080/insulin?wsdl")).getInsulinDoseCalculatorPort();
            //service = new InsulinDoseCalculatorService(new URL("http://qcs18.dei.uc.pt:8080/insulin?wsdl")).getInsulinDoseCalculatorPort();
            //service = new InsulinDoseCalculatorService(new URL("http://localhost:9000/InsulinDoseCalculator?wsdl")).getInsulinDoseCalculatorPort();
            Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
            requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, 3500); // Timeout in millis
            requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 3500);
        } catch (Exception e) {
            e.printStackTrace();
            service = null;
        }

    }

    public InsulineService setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public Integer call() throws Exception {
        connect();
        return service == null ? -1 : doWork();
    }

    protected abstract int doWork();
    protected abstract InsulineService constructNew();

}
