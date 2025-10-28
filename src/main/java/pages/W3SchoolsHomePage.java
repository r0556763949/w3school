package pages;

import co.verisoft.fw.objectrepository.ObjectRepositoryItem;
import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;


public class W3SchoolsHomePage extends BasePage{

    @ObjectRepositoryItem(id = "title")
    private WebElement title;

    @ObjectRepositoryItem(id = "HOME-SEARCH-FIELD")
    private WebElement searchField;

    @ObjectRepositoryItem(id = "HOME-SEARCH-BUTTON")
    private WebElement searchButton;

    @ObjectRepositoryItem(id = "SEARCH-RESULTS-CONTAINER")
    private WebElement resultContainer;
    @ObjectRepositoryItem(id = "SEARCH-RESULTS-ITEMS")
    private WebElement resultItems;


    public W3SchoolsHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isOnPage() {
        try {
            Waits.visibilityOf(driver, timeOutSeconds, title);
            return title.isDisplayed() && title.getText().contains("Learn to Code");
        } catch (Exception e) {
            System.out.println("Page not loaded or title element not found: " + e.getMessage());
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
            // אני לא יודעת איך מהJSON אני אשלוף רשימה? לכן:
            List<WebElement> items = resultContainer.findElements(By.cssSelector("a.search_item"));
            return items.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.info("Failed to retrieve result items: " + e.getMessage());
            return List.of();
        }
    }


    public boolean allResultsContain(String keyword) {
        List<String> results = getResultTexts();
        if (results.isEmpty()) {
            log.info("No search results found.");
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
            List<WebElement> items = resultContainer.findElements(By.cssSelector("a.search_item"));
            for (WebElement item : items) {
                if (item.getText().toLowerCase().contains(text.toLowerCase())) {
                   log.info("Clicking on result: " + item.getText());
                    item.click();
                    return;
                }
            }

            log.info("No result found containing: " + text);

        } catch (Exception e) {
           log.info("Failed to select suggestion: " + e.getMessage());
        }
    }

}
