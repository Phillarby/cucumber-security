package zap;

import java.net.MalformedURLException;

public class ClientBuilder {

    private String apiKey = Utils.EMPTY_STRING;
    private String apiProtocol = Utils.EMPTY_STRING;;
    private String apiHost = Utils.EMPTY_STRING;;
    private int apiPort = 0;
    private String apiPath = Utils.EMPTY_STRING;

    public ClientBuilder setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public ClientBuilder setApiProtocol(String apiProtocol) {
        this.apiProtocol = apiProtocol;
        return this;
    }

    public ClientBuilder setApiHost(String apiHost) {
        this.apiHost = apiHost;
        return this;
    }

    public ClientBuilder setApiPort(int apiPort) {
        this.apiPort = apiPort;
        return this;
    }

    public ClientBuilder setApiPath(String apiPath) {
        this.apiPath = apiPath;
        return this;
    }

    public Client build() throws MalformedURLException {
        return new Client(apiKey, apiProtocol, apiHost, apiPort, apiPath);
    }
}
