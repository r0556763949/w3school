import co.verisoft.fw.asserts.SoftAssertsScreenShot;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.components.LeftNavComponent;

import java.util.Arrays;
import java.util.List;

public class LeftNavTest extends BaseTest{
    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();

    @Test
    @Disabled("This test is disabled, it's only for me")
    void verifyLeftNavHeadings(VerisoftDriver driver) {
        SoftAssertsScreenShot softAssert = new SoftAssertsScreenShot(driver);

        driver.get("https://www.w3schools.com/java/default.asp");

        LeftNavComponent leftNav = new LeftNavComponent(driver);
        softAssert.assertTrue(leftNav.isDisplayed(), "Left navigation is not displayed");

        List<String> expectedMainTopics = List.of(
                "Java Tutorial", "Java Methods", "Java Classes", "Java Errors",
                "Java File Handling", "Java I/O Streams", "Java Data Structures",
                "Java Advanced", "Java Projects", "Java How To's",
                "Java Reference", "Java Examples"
        );

        softAssert.assertEquals(leftNav.getHeadings(), expectedMainTopics,
                "Main topics list does not match expected");

        List<String> expectedSubTopics1 = Arrays.asList(
                "Java HOME",
                "Java Intro",
                "Java Get Started",
                "Java Syntax",
                "Java Output",
                "Java Comments",
                "Java Variables",
                "Java Data Types",
                "Java Type Casting",
                "Java Operators",
                "Java Strings",
                "Java Math",
                "Java Booleans",
                "Java If...Else",
                "Java Switch",
                "Java While Loop",
                "Java For Loop",
                "Java Break/Continue",
                "Java Arrays"
        );

        softAssert.assertEquals(
                expectedSubTopics1,
                leftNav.getSubItemsByHeadingIndex(0),
                "Subtopics for first main topic should match expected"
        );
        softAssert.assertAll();
        driver.quit();

    }
}
