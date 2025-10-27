import co.verisoft.fw.asserts.SoftAssertsScreenShot;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import jdk.jfr.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.W3SchoolsHomePage;

import java.util.List;

@Execution(ExecutionMode.CONCURRENT)
public class HomePageTest extends BaseTest {
    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();

    @Test
    @DisplayName("open Wsachools Home")
    @Description("open home, topnav , search java , choose java-tutoril")
    public void HomeTest(VerisoftDriver driver) {

        SoftAssertsScreenShot soft = new SoftAssertsScreenShot(driver);
        driver.get("https://www.w3schools.com/");
        W3SchoolsHomePage home = new W3SchoolsHomePage(driver);
        soft.assertTrue(home.isOnPage(), "Home page not loaded - 'Learn to Code' missing");

        home.search("java");
        soft.assertTrue(home.waitForResultsToAppear(), "Search results did not appear");

        List<String> results = home.getResultTexts();
        soft.assertFalse(results.isEmpty(), "No results found");
        soft.assertTrue(home.allResultsContain("java"), "Some results do not contain 'java'");

        home.selectSuggestion("Java Tutorial");
        soft.assertTrue(
                driver.getCurrentUrl().contains("java") || driver.getCurrentUrl().contains("tutorial"),
                "Did not navigate to Java tutorial page"
        );

        soft.assertAll();
    }

}
