package pages;

import co.verisoft.fw.objectrepository.ObjectRepositoryItem;
import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class W3SchoolsJavaTutorialPage extends BasePage{
//    @ObjectRepositoryItem(id = "LEFT-MENU")
//    private WebElement leftMenu;
//    @ObjectRepositoryItem(id = "LEFT-MENU-HEADERS")
//    private List<WebElement> mainTopics;
//
//    @ObjectRepositoryItem(id = "LEFT-MENU-LINKS")
//    private List<WebElement> subTopics;
//
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


    public void clickNext() {
        nextButton.click();
    }

    public void clickPrevious() {
        prevButton.click();
    }
    public boolean verifyNextTopicNavigation() {
        int currentIndex = leftNav.getSubItemIndexByName(getPageTitleText());
        clickNext();
        Waits.pageToFullyLoad(driver, timeOutSeconds);
        int nextIndex = leftNav.getSubItemIndexByName(getPageTitleText());
        return nextIndex == currentIndex + 1;
    }

    public String getPageTitleText() {
        WebElement headingElement = driver.findElement(By.tagName("h1"));
        String mainTitle = headingElement.getText().trim();
        String subTitle = "";
        try {
            subTitle = headingElement.findElement(By.className("color_h1")).getText().trim();
        } catch (Exception e) {
            System.out.println("Sub-title not found: " + e.getMessage());
        }

        return mainTitle + subTitle;
    }

}
