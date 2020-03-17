package stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;
import zap.Client;
import zap.ClientBuilder;

import java.net.MalformedURLException;

public class ScanConfiguration {

    private static String ZAP_PROTOCOL = "http";
    private static String ZAP_ADDRESS = "localhost";
    private int ZAP_PORT = 808;
    private static String ZAP_API_KEY = "qwerty";

    private ClientApi zap;

    @Given("I have configured Zap")
    public void i_have_configured_Zap() throws MalformedURLException {

        Client zapClient = new ClientBuilder()
            .setApiProtocol(ZAP_PROTOCOL)
            .setApiHost(ZAP_ADDRESS)
            .setApiPort(ZAP_PORT)
            .setApiKey(ZAP_API_KEY)
            .build();

        zap = zapClient.getApiClient();
    }

    @When("I run it")
    public void i_run_it() throws ClientApiException, InterruptedException {

        ApiResponse resp = zap.spider.scan("http://localhost:80", null, null, null, null);

        String scanid;
        int progress;
        // The scan now returns a scan id to support concurrent scanning
        scanid = ((ApiResponseElement)resp).getValue();

        // Poll the status until it completes
        while (true) {
            Thread.sleep(1000);
            progress =
                    Integer.parseInt(
                            ((ApiResponseElement)zap.spider.status(scanid)).getValue());
            System.out.println("Spider progress : " + progress + "%");
            if (progress >= 100) {
                break;
            }
        }

    }

    @Then("I get an output")
    public void i_get_an_output() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
