package pages.components;

import co.verisoft.fw.utils.Waits;
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

    @FindBy(css = "#leftmenuinnerinner h2, #leftmenuinnerinner .left")
    private List<WebElement> headings;

    @FindBy(css = "#leftmenuinnerinner a")
    private List<WebElement> subLinks;

    public LeftNavComponent(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isDisplayed() {
        Waits.visibilityOf(this.driver, 10, container);
        return container.isDisplayed();
    }

    public List<String> getHeadings() {
        return headings.stream()
                .map(e -> e.getText().trim())
                .filter(t -> !t.isEmpty())
                .toList();
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
        subLinks.stream()
                .filter(link -> link.getText().trim().equalsIgnoreCase(subItemText))
                .findFirst()
                .ifPresent(WebElement::click);
    }

//    public int getSubItemIndexByName(String subItemText) {
//        List<String> allHeadings = getHeadings();
//        for (int headingIndex = 0; headingIndex < allHeadings.size(); headingIndex++) {
//            List<String> subItems = getSubItemsByHeadingIndex(headingIndex);
//            for (int i = 0; i < subItems.size(); i++) {
//                if (subItems.get(i).equalsIgnoreCase(subItemText.trim())) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
}
