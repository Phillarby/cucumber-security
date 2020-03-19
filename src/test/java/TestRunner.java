import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps"},
        strict=true,
        plugin = {"pretty", "html:target/reports/cucumber/html" , "junit:target/reports/cucumber/junit/cucumber.xml"}
)

public class TestRunner {

}
