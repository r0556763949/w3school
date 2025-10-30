package pages.components;

import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class TopNavComponent {
    private WebDriver driver;

    @FindBy(id = "subtopnav")
    private WebElement topNavBar;

    @FindBy(css = "#subtopnav a")
    private List<WebElement> navLinks;

    @FindBy(id = "scroll_right_btn")
    private WebElement scrollRightBtn;

    @FindBy(id = "scroll_left_btn")
    private WebElement scrollLeftBtn;


    public TopNavComponent(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public boolean isDisplayed() {
        Waits.visibilityOf(this.driver, 10, topNavBar);
        return topNavBar.isDisplayed();
    }

    public void clickLinkByText(String linkText) {
        for (WebElement link : navLinks) {
            if (link.getText().trim().equalsIgnoreCase(linkText)) {
                link.click();
                break;
            }
        }
    }

    public boolean scrollLeftAndVerify() {
        long beforeScroll = (long) ((JavascriptExecutor) driver)
                .executeScript("return document.getElementById('subtopnav').scrollLeft;");
        scrollLeftBtn.click();
        long afterScroll = (long) ((JavascriptExecutor) driver)
                .executeScript("return document.getElementById('subtopnav').scrollLeft;");
        return afterScroll < beforeScroll;
    }
    public boolean scrollRightAndVerify() {
        long beforeScroll = (long) ((JavascriptExecutor) driver)
                .executeScript("return document.getElementById('subtopnav').scrollLeft;");
        scrollRightBtn.click();
        long afterScroll = (long) ((JavascriptExecutor) driver)
                .executeScript("return document.getElementById('subtopnav').scrollLeft;");
        return afterScroll > beforeScroll;
    }
}
