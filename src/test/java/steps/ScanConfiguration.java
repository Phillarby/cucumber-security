package steps;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.zaproxy.clientapi.core.*;
import utils.Constants;
import utils.StateContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class ScanConfiguration {

    private StateContainer scenarioState;
    private ClientApi zap;

    Logger logger = LoggerFactory.getLogger(ScanConfiguration.class);

    //PicoContainer will automatically inject an instance of a state object
    //relevant to the scenario being executed.  This can be used to persist state
    //between steps.  There is a static global state object that can be used
    //to persist required state between scenarios
    public ScanConfiguration(StateContainer scenarioState) throws MalformedURLException {
        logger.debug("ScanConfiguration constructed");
        this.scenarioState = scenarioState;
        zap = (ClientApi)Constants.GLOBAL_STATE.getObject("zap");
    }

    /**
     * Check if a URL has already had a standard spider run against it in the current execution.
     * If it has then there should not be a need to re-run the spider as the pages will already
     * be cached on teh ZAP server
     * @param url the URL of the site to spider
     * @throws InterruptedException
     * @throws ClientApiException
     */
    @Given("I have spidered and passively scanned {string}")
    public void TestSpiderSpecifiedUrl(String url) throws InterruptedException, ClientApiException {

        //Presence of a spider key for the URL in the global state file indicates the URL has been
        //spidered and does not need to be redone.  If the key is not present then initiate the spidering
        boolean spiderNeeded = !Constants.GLOBAL_STATE.hasKey(getSpiderStateKey(url));
        logger.info("Tested if spidering is needed for {}: {}", url, spiderNeeded);
        if (spiderNeeded) SpiderSpecifiedUrl(url);
    }

    /**
     * gets the state key associated with the specified URL that records whether it has already been spidered
     * @param url the target URL
     */
    private String getSpiderStateKey(String url) {
        return String.format("spider %s", url);
    }

    /**
     * gets the state key associated with the specified URL that records whether it has already been
     * subject to a passive scan
     * @param url the target URL
     */
    private String getPassiveScanStateKey(String url) {
        return String.format("passive %s", url);
    }

    /**
     * Runs a standard spider against a specified URL
     * @param url the target URL
     * @throws ClientApiException
     * @throws InterruptedException
     */
    private void SpiderSpecifiedUrl(String url) throws ClientApiException, InterruptedException {

        logger.debug("Initiating standard spider against {}", url);
        ApiResponse resp = zap.spider.scan(url, null, null, null, null);

        int progress = 0;
        int progressPollRate = 1000;

        // Get the identifier for the current scan and add to scenario state for progress monitoring
        String scanid = ((ApiResponseElement)resp).getValue();
        logger.debug("Spider running with scan id {}", scanid);
        logger.debug("Polling progress every {} milliseconds", progressPollRate);
        scenarioState.add("scanid", scanid);

        // Poll the status until the spider completes
        while (progress < 100) {
            Thread.sleep(progressPollRate);
            progress = Integer.parseInt(((ApiResponseElement)zap.spider.status(scanid)).getValue());
            logger.debug("Spider progress {}%", progress);
        }
        logger.debug("Spider complete");
        scenarioState.add(getSpiderStateKey(url), true);

        ApiResponse response = zap.spider.results(scenarioState.getString("scanid"));
        logger.debug("Spider found {} pages", ((ApiResponseList)(response)).getItems().size() );

        waitForPassiveScan(url);
    }

    /**
     * Wait for any ongoing passive scan to complete
     * @param url the target URL
     */
    private void waitForPassiveScan(String url) throws ClientApiException {

        logger.debug("Waiting for passive scan to complete");
        int scanRemaining = 1;
        while (scanRemaining > 0) {
            int lastpoll = scanRemaining;
            zap.pscan.recordsToScan(); //Not sure why need this - is in examples, and seems to give more accurate results
            scanRemaining = Integer.parseInt(((ApiResponseElement) zap.pscan.recordsToScan()).getValue());
            if (lastpoll != scanRemaining) logger.debug("{} pages remaining to scan", scanRemaining);
        }
        logger.debug("passive scan complete");
        scenarioState.add(getPassiveScanStateKey(url), true);
    }

    /**
     * Retreives the number alerts split int high, medium, low and informational categories
     * @throws ClientApiException
     */
    @When("I get the results")
    public void iGetTheResultsCount() throws ClientApiException {
        ApiResponseSet alertCountsByRisk = (ApiResponseSet)zap.alert.alertCountsByRisk(null, null);
        
        int high = Integer.parseInt(alertCountsByRisk.getValue("High").toString());
        int medium = Integer.parseInt(alertCountsByRisk.getValue("Medium").toString());
        int low = Integer.parseInt(alertCountsByRisk.getValue("Low").toString());
        int informational = Integer.parseInt(alertCountsByRisk.getValue("Informational").toString());
        
        scenarioState.add("pscanHigh", high);
        scenarioState.add("pscanMedium", medium);
        scenarioState.add("pscanLow", low);
        scenarioState.add("pscanInformational", informational);
    }

    /**
     * Asserts that a specific number of alerts of a specific type have been reported
     * @param count
     * @param risk
     */
    @Then("there are/is {int} {word} risk alert(s)")
    public void validateNumberOfAlerts(int count, String risk) {

        int alerts;
        switch(risk.toLowerCase()) {
            case "high":
                alerts = scenarioState.getInt("pscanHigh");
                break;
            case "medium":
                alerts = scenarioState.getInt("pscanMedium");
                break;
            case "low":
                alerts = scenarioState.getInt("pscanLow");
                break;
            case "informational":
                alerts = scenarioState.getInt("pscanInformational");
                break;
            default:
                String message = String.format("Invalid risk parameter specified: %s", risk);
                throw new IllegalArgumentException();
        }

        Assert.assertEquals(count, alerts);
    }

    /**
     * Retreives and saves the HTML report with the specified filename
     * @param filename the sane to use when saving the report
     * @throws IOException
     * @throws ClientApiException
     */
    @And("save the HTML report as {string}")
    public void saveTheHTMLReport(String filename) throws IOException, ClientApiException {

        File targetClassesDir = new File(ScanConfiguration.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File target = targetClassesDir.getParentFile();
        String reportPath = String.format("/reports/zap/%s", filename);
        target = new File(target.toString().concat(reportPath));
        target.getParentFile().mkdirs();

        byte[] reportBytes = zap.core.htmlreport();

        FileOutputStream out = new FileOutputStream(target);
        out.write(reportBytes);
        out.close();
    }


    /**
     * Initiates an active scan against a specified URL. This involves constructing intentionally malicious
     * calls to identified potentially vulnerable pages and recored results.  Note this scan can take a long
     * time to execute.
     *
     * @param url the URL to be scanned
     * @throws ClientApiException
     * @throws InterruptedException
     */
    @And("I actively scan {string}")
    public void performActiveScan(String url) throws ClientApiException, InterruptedException {

        ApiResponse resp = zap.ascan.scan(url,"True", "False", null, null, null);
        String scanid = ((ApiResponseElement) resp).getValue();

        int progress = 0;
        int progressPollRate = 10000;
        while (progress < 100) {
            Thread.sleep(progressPollRate);
            progress = Integer.parseInt(((ApiResponseElement)zap.ascan.status(scanid)).getValue());
            logger.debug("Active scan {}%", progress);
        }

        logger.debug("Active scan complete");
    }

    /**
     * Runs the AJAX spider against the specified URL. Follows this up by also running a standard
     * spider and passive scan for full coverage
     *
     * @param url The URL to be scanned by the spider
     * @throws ClientApiException
     * @throws InterruptedException
     */
    @Given("I have ajax spidered and passively scanned {string}")
    public void performAjaxSpider(String url) throws ClientApiException, InterruptedException {
        logger.debug("Starting the ZAP spider for {}", url);
        ApiResponse resp = zap.ajaxSpider.scan(url, null, null, null);

        int progressPollRate = 1000;

        // Get the identifier for the current scan
        String scanid = ((ApiResponseElement)resp).getValue();
        logger.debug("ZAP spider running with scan id {}", scanid);
        logger.debug("Polling spider progress every {} milliseconds", progressPollRate);
        scenarioState.add("scanid", scanid);

        String spiderStatus;
        while(!(spiderStatus = zap.ajaxSpider.status().toString()).equals("stopped")) {
            Thread.sleep(progressPollRate);
            logger.info("Ajax spider status: {}", spiderStatus);
        }

        logger.debug("Ajax spider completed. {} hits found", zap.ajaxSpider.numberOfResults());
        logger.info("initiating standard spider scan to supplement ajax spider results");

        TestSpiderSpecifiedUrl(url);
    }
}
