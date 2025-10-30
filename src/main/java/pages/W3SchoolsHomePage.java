package pages;

import co.verisoft.fw.objectrepository.ObjectRepositoryItem;
import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;


public class W3SchoolsHomePage extends BasePage{

    @FindBy(css = "h1.learntocodeh1")
    private WebElement title;

    @FindBy(id = "search2")
    private WebElement searchField;

    @FindBy(id = "learntocode_searchbtn")
    private WebElement searchButton;

    @FindBy(id = "listofsearchresults")
    private WebElement resultContainer;

    @FindBy(css = "a.search_item")
    private List<WebElement> resultItems;


    public W3SchoolsHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isOnPage() {
        try {
            Waits.visibilityOf(driver, timeOutSeconds, title);
            return title.isDisplayed() && title.getText().contains("Learn to Code");
        } catch (Exception e) {
            log.info("Page not loaded or title element not found: " + e.getMessage());
            return false;
        }
    }


    public void search(String text) {
        searchField.clear();
        searchField.sendKeys(text);
    }

    public boolean waitForResultsToAppear() {
        try {
            Waits.visibilityOf(driver, timeOutSeconds, resultContainer);
            return isElementDisplayed(resultContainer);
        } catch (Exception e) {
            log.info("no reasult appeared");
            return false;
        }
    }
    public List<String> getResultTexts() {
        try {
            Waits.visibilityOf(driver, timeOutSeconds, resultContainer);
            return resultItems.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve result items: " + e.getMessage());
            return List.of();
        }
    }


    public boolean allResultsContain(String keyword) {
        List<String> results = getResultTexts();
        if (results.isEmpty()) {
            log.error("No search results found.");
            return false;
        }
        boolean allMatch = results.stream()
                .allMatch(text -> text.toLowerCase().contains(keyword.toLowerCase()));

        if (!allMatch) {
            log.info("Some results do not contain the keyword '" + keyword + "'");
            results.forEach(System.out::println);
        }

        return allMatch;
    }

    public void selectSuggestion(String text) {
        try {
            Waits.visibilityOf(driver, timeOutSeconds, resultContainer);
            for (WebElement item : resultItems) {
                if (item.getText().toLowerCase().contains(text.toLowerCase())) {
                   log.info("Clicking on result: " + item.getText());
                    item.click();
                    return;
                }
            }

            log.error("No result found containing: " + text);

        } catch (Exception e) {
           log.error("Failed to select suggestion: " + e.getMessage());
        }
    }

}
