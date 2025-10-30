import co.verisoft.fw.asserts.SoftAssertsScreenShot;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import co.verisoft.fw.utils.Waits;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.W3SchoolsJavaTutorialPage;
import pages.components.LeftNavComponent;

import java.nio.file.Path;
import java.util.*;

@Execution(ExecutionMode.CONCURRENT)
public class JavaTutorialTest extends BaseTest {
    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();
    // אני צריכה ייעוץ מה לעשות לגבי זה שהטסטים מאד ארוכים

//    @ParameterizedTest
//    @MethodSource("provideTopics")
//    @DisplayName("open JavaTutorial page and verify")
//    @Description("in java page, leftnav, maintopics, subtopics for 2 of maintopics ")
//    public void verifyJavaTutorialLeftMenu(Map.Entry<String, List<String>> topicEntry, VerisoftDriver driver) {
//
//        SoftAssertsScreenShot softAssert = new SoftAssertsScreenShot(driver);
//        driver.get("https://www.w3schools.com/java/default.asp");;
//        W3SchoolsJavaTutorialPage javaPage = new W3SchoolsJavaTutorialPage(driver);
//
//        softAssert.assertTrue(javaPage.isOnPage(), "User should be on Java Tutorial page");
//        softAssert.assertTrue(javaPage.isLeftNavComponentDisplayed(), "Left Nav Component is not displayed");
//        softAssert.assertTrue(javaPage.isTopNavComponentDisplayed(), "Top Nav Component is not displayed");
//        LeftNavComponent leftnNav = javaPage.getLeftNav();
//
//        String mainTopic = topicEntry.getKey();
//        List<String> expectedSubTopics = topicEntry.getValue();
//
//        List<String> actualMainTopics = leftnNav.getHeadings();
//        int index = actualMainTopics.indexOf(mainTopic);
//        softAssert.assertTrue(index >= 0, "Main topic '" + mainTopic + "' not found in the left nav");
//
//        if (index < 2) {
//        List<String> actualSubTopics = leftnNav.getSubItemsByHeadingIndex(index);
//            softAssert.assertEquals(
//                    actualSubTopics,
//                    expectedSubTopics,
//                    "Subtopics for '" + mainTopic + "' should match expected"
//            );
//        }
//
//softAssert.assertTrue(
//        javaPage.verifyNextTopicNavigation() ,
//        "Did not navigate to next topic"
//        );
//        javaPage.clickPreviousAndVerify();
//        javaPage.getTopNav().clickLinkByText("html");
//        softAssert.assertTrue(
//                driver.getCurrentUrl().contains("html") ,
//                "Did not navigate to html tutorial page"
//                        );
//        softAssert.assertAll();
//    }
    @Test
    public void verifyJavaTutorialLeftMenu( VerisoftDriver driver) {

        SoftAssertsScreenShot softAssert = new SoftAssertsScreenShot(driver);
        driver.get("https://www.w3schools.com/java/default.asp");;
        W3SchoolsJavaTutorialPage javaPage = new W3SchoolsJavaTutorialPage(driver);

        softAssert.assertTrue(javaPage.isOnPage(), "User should be on Java Tutorial page");
        softAssert.assertTrue(javaPage.isLeftNavComponentDisplayed(), "Left Nav Component is not displayed");
        softAssert.assertTrue(javaPage.isTopNavComponentDisplayed(), "Top Nav Component is not displayed");
        LeftNavComponent leftnNav = javaPage.getLeftNav();

        softAssert.assertTrue(
                javaPage.verifyNextTopicNavigation() ,
                "Did not navigate to next topic"
        );
        javaPage.clickPreviousAndVerify();
        javaPage.getTopNav().clickLinkByText("html");
        softAssert.assertTrue(
                driver.getCurrentUrl().contains("html") ,
                "Did not navigate to html tutorial page"
        );
        softAssert.assertAll();
    }
    public static Map<String, List<String>> loadData(Path jsonPath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(
                    jsonPath.toFile(),
                    mapper.getTypeFactory().constructMapType(Map.class, String.class, List.class)
            );
        } catch (Exception e) {
            System.err.println(" Failed to load JSON data from " + jsonPath + ": " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    static List<Map.Entry<String, List<String>>> provideTopics() {
        Path path = Path.of("src/test/resources/javaTopics.json");
        return new ArrayList<>(loadData(path).entrySet());
    }
}
