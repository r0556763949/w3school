import co.verisoft.fw.asserts.SoftAssertsScreenShot;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import co.verisoft.fw.utils.Waits;
import jdk.jfr.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.W3SchoolsJavaTutorialPage;
import pages.components.LeftNavComponent;

import java.util.Arrays;
import java.util.List;

public class JavaTutorialTest extends BaseTest {
    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();

    @Test
    @DisplayName("open JavaTutorial page and verify")
    @Description("in java page, leftnav, maintopics, subtopics for 2 of maintopics ")
    public void verifyJavaTutorialLeftMenu(VerisoftDriver driver) {

        SoftAssertsScreenShot softAssert = new SoftAssertsScreenShot(driver);
        driver.get("https://www.w3schools.com/java/default.asp");;
        W3SchoolsJavaTutorialPage javaPage = new W3SchoolsJavaTutorialPage(driver);

        softAssert.assertTrue(javaPage.isOnPage(), "User should be on Java Tutorial page");
        softAssert.assertTrue(javaPage.isLeftNavComponentDisplayed(), "Left Nav Component is not displayed");
        softAssert.assertTrue(javaPage.isTopNavComponentDisplayed(), "Top Nav Component is not displayed");
        LeftNavComponent leftnNav = javaPage.getLeftNav();

        List<String> expectedMainTopics = List.of(
                "Java Tutorial",
                "Java Methods",
                "Java Classes",
                "Java Errors",
                "Java File Handling",
                "Java I/O Streams",
                "Java Data Structures",
                "Java Advanced",
                "Java Projects",
                "Java How To's",
                "Java Reference",
                "Java Examples"
        );

        List<String> actualMainTopics = leftnNav.getHeadings();
        softAssert.assertFalse(
                actualMainTopics.isEmpty(),
                "No main topics were returned from the page!"
        );
        softAssert.assertTrue(
                actualMainTopics.containsAll(expectedMainTopics),
                "Main topics should match expected ones. Actual: " + actualMainTopics
        );

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

        List<String> expectedSubTopics2 = Arrays.asList(
                "Java Methods",
                "Java Method Parameters",
                "Java Method Overloading",
                "Java Scope",
                "Java Recursion"
        );

        List<String> actualSubTopics1 = leftnNav.getSubItemsByHeadingIndex(0);
         List<String> actualSubTopics2 = leftnNav.getSubItemsByHeadingIndex(1);

        softAssert.assertEquals(
                actualSubTopics1,
                expectedSubTopics1,
                "Subtopics for first main topic should match expected"
        );

        softAssert.assertEquals(
                actualSubTopics2,
                expectedSubTopics2,
                "Subtopics for second main topic should match expected"
        );
      //  javaPage.verifyNextTopicNavigation();
        softAssert.assertAll();
    }
}
