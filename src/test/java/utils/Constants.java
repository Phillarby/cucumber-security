package utils;

import zap.Client;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;

public class Constants {

    static {
        StateContainer state = new StateContainer();

        try {
            Client apiClient = new Client();
            state.add("zap", apiClient.getApiClient());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GLOBAL_STATE = state;
    }

    //todo: Singleton global state object
    public static final StateContainer GLOBAL_STATE;
    public static final String EMPTY_STRING = "";


}
