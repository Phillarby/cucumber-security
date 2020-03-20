package zap;

import org.zaproxy.clientapi.core.ClientApi;
import utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Client {

    private ClientApi clientApi;

    public Client() throws IOException {

        //load API configuration from properties file
        Properties clientApiProperties = new Properties();
        String zapPropertiesFile = "zap.properties";
        InputStream zapProperties = Client.class.getClassLoader().getResource(zapPropertiesFile).openStream();
        clientApiProperties.load(zapProperties);

        String apiKey = clientApiProperties.getProperty("apiKey");
        String apiHost = clientApiProperties.getProperty("apiHost");
        int apiPort = Integer.parseInt(clientApiProperties.getProperty("apiPort"));

        clientApi = new ClientApi(apiHost, apiPort, apiKey);
    }

    public ClientApi getApiClient() {
        return clientApi;
    }
}
