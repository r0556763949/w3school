import co.verisoft.fw.asserts.SoftAssertsScreenShot;
import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.HTMLTablesPage;

import java.util.List;

public class HTMLTablesPageTest extends BaseTest{

    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();

    @Test
    public void verifyHtmlTableContent(VerisoftDriver driver) {
        SoftAssertsScreenShot softAssert = new SoftAssertsScreenShot(driver);
        driver.get("https://www.w3schools.com/html/html_tables.asp");
        HTMLTablesPage htmlTablesPage = new HTMLTablesPage(driver);

        softAssert.assertTrue(htmlTablesPage.isOnPage(), "HTML Tables page should be displayed");

        List<String> expectedHeaders = List.of("Company", "Contact", "Country");
        softAssert.assertEquals(
                htmlTablesPage.getColumnHeaders(),
                expectedHeaders,
                "Table headers should match expected"
        );

        List<String> firstRow = htmlTablesPage.getContactAndCountryByRowIndex(1);
        List<String> secondRow = htmlTablesPage.getContactAndCountryByRowIndex(2);

        softAssert.assertEquals(firstRow, List.of("Maria Anders", "Germany"), "Row 1 data mismatch");
        softAssert.assertEquals(secondRow, List.of("Francisco Chang", "Mexico"), "Row 2 data mismatch");


        softAssert.assertAll();
    }
}
