package steps;

import io.cucumber.java.Before;
import java.io.IOException;

public class TestHooks {


    @Before
    public static void main(String[] args) {
        try {
            Runtime runtime = Runtime.getRuntime();

            String loginScript = "Legal1.4ZAP.exe";

            Process process = runtime.exec(loginScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}