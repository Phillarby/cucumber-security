import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps"},
        strict=true,
        plugin = {"pretty", "html:target/reports/html" , "junit:target/reports/junit/cucumber.xml"}
)

public class TestRunner {

}
