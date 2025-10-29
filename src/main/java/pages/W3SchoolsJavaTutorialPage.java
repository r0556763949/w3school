package pages;

import co.verisoft.fw.objectrepository.ObjectRepositoryItem;
import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class W3SchoolsJavaTutorialPage extends BasePage{

    @FindBy(css = "a.w3-right.w3-btn[href*='java_']")
    private WebElement nextButton;
    @FindBy(xpath = "(//div[@class='w3-clear nextprev']//a[contains(@class,'w3-left')])[1]")
    private WebElement prevButton;
    @FindBy(css = "h1.with-bookmark")
    private WebElement headingElement;

    @FindBy(css = "h1.with-bookmark .color_h1")
    private WebElement subHeadingElement;


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
        Waits.visibilityOf(driver, timeOutSeconds, prevButton);
        prevButton.click();
    }
    public boolean verifyNextTopicNavigation() {
        String nextHref = nextButton.getAttribute("href");
        clickNext();
        Waits.pageToFullyLoad(driver, timeOutSeconds);
        return driver.getCurrentUrl().contains(nextHref);
//        int currentIndex = leftNav.getSubItemIndexByName(getPageTitleText());
//        clickNext();
//        Waits.pageToFullyLoad(driver, timeOutSeconds);
//        leftNav.isDisplayed();
//        int nextIndex = leftNav.getSubItemIndexByName(getPageTitleText());
//        return nextIndex == currentIndex + 1;
    }
    public boolean clickPreviousAndVerify() {
        String prevHref = prevButton.getAttribute("href");
        clickPrevious();
        Waits.pageToFullyLoad(driver, timeOutSeconds);
        return driver.getCurrentUrl().contains(prevHref);
//        int currentIndex = leftNav.getSubItemIndexByName(getPageTitleText());
//        if (currentIndex == 0) {
//            log.info("Can't go back. This is the first sub-topic.");
//            return false;
//        }
//        clickPrevious();
//        Waits.pageToFullyLoad(driver, timeOutSeconds);
//        int previousIndex = leftNav.getSubItemIndexByName(getPageTitleText());
//        return previousIndex == currentIndex - 1;
    }

    public String getPageTitleText() {
        String mainTitle = headingElement.getText().trim();
        String subTitle = "";
        try {
            subTitle = subHeadingElement.getText().trim();
        } catch (Exception e) {
            System.out.println("Sub-title not found: " + e.getMessage());
        }

        return (mainTitle + " " + subTitle).trim();
    }

}
