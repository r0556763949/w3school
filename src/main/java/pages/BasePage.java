package pages;

import co.verisoft.fw.objectrepository.ObjectReporsitoryFactory;
import co.verisoft.fw.utils.Property;
import co.verisoft.fw.utils.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.PageFactory;
import pages.components.LeftNavComponent;
import pages.components.TopNavComponent;


public abstract class BasePage {
    // private static final Logger log = LoggerFactory.getLogger(BasePage.class);

    protected  WebDriver driver;
    protected final int timeOutSeconds;
    protected final int pollingMillis;
    protected final String objectRepositoryPath;
    protected TopNavComponent topNav;
    protected LeftNavComponent leftNav;

    public BasePage(WebDriver driver) {
        this(driver, null);
        this.topNav = new TopNavComponent(driver);
        this.leftNav = new LeftNavComponent(driver);
    }

    public BasePage(WebDriver driver, String objectRepositoryFilePath) {
        this.driver = driver;
        Property p = new Property();
        this.timeOutSeconds = p.getIntProperty("selenium.wait.timeout");
        this.pollingMillis = p.getIntProperty("polling.interval");
        String repo = p.getProperty("object.repository.path");
        if (objectRepositoryFilePath != null && !objectRepositoryFilePath.isEmpty()) {
            this.objectRepositoryPath = objectRepositoryFilePath;
            System.out.println("Object repository path in use: " + this.objectRepositoryPath);
        } else if (repo != null && !repo.isEmpty()) {
            System.out.println("repo!!!!!!!!! "+ repo);
            this.objectRepositoryPath = repo;
        } else {
             Property defaults = new Property("default.config.properties");
            String def = defaults.getProperty("object.repository.path");
            if (def == null) {
                throw new RuntimeException("object.repository.path not configured in root or default config");
            }
            this.objectRepositoryPath = def;
        }
        initElementsPageFactory(driver);
        ObjectReporsitoryFactory.initObjects(driver, this, this.objectRepositoryPath);
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
    public boolean isOnPage(WebElement... elements) {
        return this.isOnPage(this.timeOutSeconds, elements);
    }

    public boolean isOnPage(By locator) {
        try {
            Waits.visibilityOfAllElementsLocatedBy(this.driver, this.timeOutSeconds, locator);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    public boolean isOnPage(int timeout, WebElement... elements) {
        try {
            Waits.visibilityOfAllElements(this.driver, timeout, elements);
            return true;
        } catch (Exception var4) {
            return false;
        }
    }

    public boolean urlContains(String fraction) {
        try {
            Waits.urlContains(driver, Math.max(1, timeOutSeconds / 10), fraction);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public abstract boolean isOnPage();
}
