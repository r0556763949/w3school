package pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

public class LeftNavComponent {
    private final WebDriver driver;
    @FindBy(id = "leftmenuinnerinner")
    private WebElement container;

    public LeftNavComponent(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isDisplayed() {
        return container.isDisplayed();
    }

    public List<String> getHeadings() {
        List<String> headings = new ArrayList<>();
        List<WebElement> children = container.findElements(By.xpath("./*"));
        for (WebElement child : children) {
            String tag = child.getTagName().toLowerCase();
            if ("h2".equals(tag) || child.getAttribute("class") != null && child.getAttribute("class").contains("left")) {
                String t = child.getText().trim();
                if (!t.isEmpty()) headings.add(t);
            }
        }
        return headings;
    }

    public List<String> getSubItemsByHeadingIndex(int headingIndex) {
        List<String> subItems = new ArrayList<>();
        if (headingIndex < 0) return subItems;

        List<WebElement> children = container.findElements(By.xpath("./*"));
        int currentHeading = -1;
        boolean collect = false;

        for (WebElement child : children) {
            String tag = child.getTagName().toLowerCase();

            if ("h2".equals(tag) || (child.getAttribute("class") != null && child.getAttribute("class").contains("left"))) {
                currentHeading++;
                collect = currentHeading == headingIndex;
                if (currentHeading > headingIndex) break;
                continue;
            }

            if (collect && "a".equals(tag)) {
                String text = child.getText().trim();
                if (!text.isEmpty()) subItems.add(text);
            }
        }
        return subItems;
    }

    public void clickSubItem(String subItemText) {
        WebElement link = container.findElement(By.xpath(".//a[normalize-space(text())='" + subItemText + "']"));
        link.click();
    }
    public int getSubItemIndexByName(String subItemText) {
        // מעבר על כל הכותרות והחיפוש אחר תתי הנושאים
        List<String> allHeadings = getHeadings();  // קבל את כל הכותרות

        for (int headingIndex = 0; headingIndex < allHeadings.size(); headingIndex++) {
            List<String> subItems = getSubItemsByHeadingIndex(headingIndex);  // קבל את תתי הנושאים לפי הכותרת
            for (int i = 0; i < subItems.size(); i++) {
                // אם תת-הנושא נמצא
                if (subItems.get(i).equalsIgnoreCase(subItemText.trim())) {
                    return i;  // מחזיר את האינדקס אם נמצא
                }
            }
        }

        // אם לא נמצא, מחזיר -1
        return -1;
    }
}
