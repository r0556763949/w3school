package pages;

import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.stream.Collectors;

public class HTMLTablesPage extends BasePage{
    @FindBy(id = "customers")
    private WebElement table;

    @FindBy(css = "#customers th")
    private List<WebElement> headers;

    @FindBy(css = "#customers tbody tr")
    private List<WebElement> rows;

    public HTMLTablesPage(WebDriver driver) {
        super(driver);
          PageFactory.initElements(driver, this);
    }

    @Override
    public boolean isOnPage() {
        try {
            WebElement visibleTable = Waits.visibilityOf(driver, timeOutSeconds, table);
            return visibleTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public List<String> getColumnHeaders() {
        return headers.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> getContactAndCountryByRowIndex(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            throw new IllegalArgumentException("Invalid row index: " + rowIndex);
        }

        List<WebElement> cells = rows.get(rowIndex).findElements(By.tagName("td"));
        if (cells.size() < 3) {
            throw new IllegalStateException("Row doesn't contain enough cells");
        }

        String contact = cells.get(1).getText().trim();
        String country = cells.get(2).getText().trim();

        return List.of(contact, country);
    }
}
