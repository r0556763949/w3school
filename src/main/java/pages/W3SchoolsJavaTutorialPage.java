package pages;

import co.verisoft.fw.objectrepository.ObjectRepositoryItem;
import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class W3SchoolsJavaTutorialPage extends BasePage{
    @ObjectRepositoryItem(id = "LEFT-MENU")
    private WebElement leftMenu;
    @ObjectRepositoryItem(id = "LEFT-MENU-HEADERS")
    private List<WebElement> mainTopics;

    @ObjectRepositoryItem(id = "LEFT-MENU-LINKS")
    private List<WebElement> subTopics;

    @ObjectRepositoryItem(id = "NEXT-BUTTON")
    private WebElement nextButton;

    @ObjectRepositoryItem(id = "PREV-BUTTON")
    private WebElement prevButton;

    public W3SchoolsJavaTutorialPage(WebDriver driver) {
        super(driver);
    }
    @Override
    public boolean isOnPage() {
        return urlContains("java/default.asp");
    }

    public List<String> getMainTopics() {
        Waits.visibilityOf(driver, timeOutSeconds, leftMenu);
        Waits.visibilityOfAllElements(driver, timeOutSeconds,mainTopics);
        System.out.println("mainTopics:::::::::" + mainTopics);
        return mainTopics.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }


    public List<String> getSubTopicsForHeader(int headerIndex) {
        Waits.visibilityOf(driver, timeOutSeconds, leftMenu);

        if (headerIndex < 0 || headerIndex >= mainTopics.size()) {
            throw new IllegalArgumentException("Invalid header index: " + headerIndex);
        }

        WebElement headerElement = mainTopics.get(headerIndex);

//        List<WebElement> allElements = driver.findElements(
//                By.xpath("//h2[@class='left'] | //a[@target='_top']")
//        );

        List<String> childrenTexts = new ArrayList<>();
        boolean collect = false;

        for (WebElement el : subTopics) {
            if (el.equals(headerElement)) {
                collect = true;
                continue;
            }
            if (collect) {
                if (el.getTagName().equalsIgnoreCase("h2")) {
                    break;
                }
                childrenTexts.add(el.getText().trim());
            }
        }

        return childrenTexts.stream()
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }


//    public boolean verifyMenuTitles(List<String> expectedTitles) {
//        List<String> actual = getMainTopics();
//        return actual.containsAll(expectedTitles);
//    }


    public void clickNext() {
        nextButton.click();
    }

    public void clickPrevious() {
        prevButton.click();
    }
}
