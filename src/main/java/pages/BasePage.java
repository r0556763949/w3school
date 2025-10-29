package pages;

import co.verisoft.fw.objectrepository.ObjectReporsitoryFactory;
import co.verisoft.fw.utils.Property;
import co.verisoft.fw.utils.Waits;
import lombok.Generated;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.components.LeftNavComponent;
import pages.components.TopNavComponent;

import java.util.Arrays;


public abstract class BasePage {
    @Generated
    protected static final Logger log = LoggerFactory.getLogger(BasePage.class);

    protected WebDriver driver;
    protected final int timeOutSeconds;
    protected final int pollingMillis;

    protected TopNavComponent topNav;
    protected LeftNavComponent leftNav;


    public BasePage(WebDriver driver) {
        this.driver = driver;
        Property p = new Property();
        this.timeOutSeconds = p.getIntProperty("selenium.wait.timeout");
        this.pollingMillis = p.getIntProperty("polling.interval");
        this.topNav = new TopNavComponent(driver);
        this.leftNav = new LeftNavComponent(driver);
        initElementsPageFactory(driver);
        try {
            Waits.pageToFullyLoad(driver, timeOutSeconds);
        } catch (Exception e) {
            System.out.println("Warning: page did not fully load: " + e.getMessage());
        }
    }

    private void initElementsPageFactory(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTopNavComponentDisplayed() {
        return topNav.isDisplayed();
    }

    public boolean isLeftNavComponentDisplayed() {
        return leftNav.isDisplayed();
    }

    public LeftNavComponent getLeftNav() {
        return this.leftNav;
    }

    public TopNavComponent getTopNav() {
        return this.topNav;
    }

    public boolean isOnPage(WebElement... elements) {
        return this.isOnPage(this.timeOutSeconds, elements);
    }

    public boolean isOnPage(By locator) {
        try {
            Waits.visibilityOfAllElementsLocatedBy(this.driver, this.timeOutSeconds, locator);
            log.info("elements " + String.valueOf(locator) + " was present on page");
            return true;
        } catch (Exception var3) {
            log.info("elements " + String.valueOf(locator) + " wasn't present on page");
            return false;
        }
    }
    public boolean isOnPage(int timeout, WebElement... elements) {
        try {
            Waits.visibilityOfAllElements(this.driver, timeout, elements);
            log.info("elements " + Arrays.toString(elements) + "was present on page");
            return true;
        } catch (Exception var4) {
            log.info("elements " + Arrays.toString(elements) + "wasn't present on page");
            return false;
        }
    }

    public boolean urlContains(String fraction) {
        try {
            Waits.urlContains(driver, Math.max(1, timeOutSeconds / 10), fraction);
            return true;
        } catch (Exception e) {
            log.info("url isn't correct");
            return false;
        }
    }

    public abstract boolean isOnPage();
}
