package steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.zaproxy.clientapi.core.*;
import zap.Client;
import zap.ClientBuilder;

import java.net.MalformedURLException;
import java.util.List;

public class ScanConfiguration {

    private static String ZAP_PROTOCOL = "http";
    private static String ZAP_ADDRESS = "localhost";
    private static int ZAP_PORT = 8080;
    private static String ZAP_API_KEY = "qwerty";

    private ScenarioState state;
    private ClientApi zap;

    Logger logger = LoggerFactory.getLogger(ScanConfiguration.class);

    //PicoContainer will automatically inject an instance of a state object
    //relevant to the scenario being executed
    public ScanConfiguration(ScenarioState state) {
        logger.debug("ScanConfiguration constructed");
        this.state = state;
    }

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

    @Given("I have spidered {string}")
    public void SpiderSpecifiedUrl(String url) throws ClientApiException, InterruptedException {

        logger.debug("Starting the ZAP spider for {}", url);
        ApiResponse resp = zap.spider.scan(url, null, null, null, null);

        int progress = 0;
        int progressPollRate = 1000;

        // Get the identifier for the current scan
        String scanid = ((ApiResponseElement)resp).getValue();
        logger.debug("ZAP spider running with scan id {}", scanid);
        logger.debug("Polling spider progress every {} milliseconds", progressPollRate);
        state.add("scanid", scanid);

        // Poll the status until the spider completes
        while (progress < 100) {
            Thread.sleep(progressPollRate);
            progress = Integer.parseInt(((ApiResponseElement)zap.spider.status(scanid)).getValue());
            logger.debug("Spider progress {}%", progress);
        }
        logger.debug("Spider complete");

        ApiResponse response = zap.spider.results(state.getString("scanid"));
        logger.debug("Spider found {} pages", ((ApiResponseList)(response)).getItems().size() );

        logger.debug("Waiting for passive scan to complete");
        int scanRemaining = 1;
        while (scanRemaining > 0) {
            int lastpoll = scanRemaining;
            zap.pscan.recordsToScan(); //Not sure why need this - is in examples, and seems to give better results
            scanRemaining = Integer.parseInt(((ApiResponseElement) zap.pscan.recordsToScan()).getValue());
            if (lastpoll != scanRemaining) logger.debug("{} pages remaining to scan", scanRemaining);
        }
        logger.debug("passive scan complete");
    }
}
