package zap;

import org.zaproxy.clientapi.core.ClientApi;

import java.net.MalformedURLException;

public class Client {

    private ClientApi clientApi;

    private String apiKey = Utils.EMPTY_STRING;;
    private String apiProtocol = Utils.EMPTY_STRING;;
    private String apiHost = Utils.EMPTY_STRING;;
    private int apiPort = 0;
    private String apiPath = Utils.EMPTY_STRING;

    public Client(String apiKey, String apiProtocol, String apiHost, int apiPort, String apiPath) throws MalformedURLException {
        this.apiKey = apiKey;
        this.apiProtocol = apiProtocol;
        this.apiHost = apiHost;
        this.apiPort = apiPort;
        this.apiPath = apiPath;

        clientApi = new ClientApi(String.format(apiHost), apiPort, apiKey);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiProtocol() {
        return apiProtocol;
    }

    public void setApiProtocol(String apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public ClientApi getApiClient() {
        return clientApi;
    }
}
