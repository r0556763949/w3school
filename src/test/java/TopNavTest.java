import co.verisoft.fw.asserts.SoftAssertsScreenShot;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.components.TopNavComponent;

public class TopNavTest extends BaseTest{
    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();

    @Test
    void verifyTopNavAndHtmlNavigation(VerisoftDriver driver) {
        SoftAssertsScreenShot softAssert = new SoftAssertsScreenShot(driver);

        driver.get("https://www.w3schools.com/");
        TopNavComponent topNav = new TopNavComponent(driver);

        softAssert.assertTrue(topNav.isDisplayed(), "Top navigation bar is not displayed");

        topNav.clickLinkByText("HTML");

        softAssert.assertTrue(
                driver.getCurrentUrl().contains("/html/default.asp"),
                "Did not navigate to the HTML tutorial page"
        );


        softAssert.assertAll();
        driver.quit();
    }
}
