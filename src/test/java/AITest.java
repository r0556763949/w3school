import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import co.verisoft.fw.selenium.drivers.factory.DriverCapabilities;
import config.DynamicExecutor;
import config.TryAI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.chrome.ChromeOptions;
import co.verisoft.fw.extentreport.DelegateExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AITest extends BaseTest{
    @DriverCapabilities
    private final ChromeOptions capabilities = new ChromeOptions();

    private static final Logger log = LoggerFactory.getLogger(AITest.class);
    private final List<String> aiLogBuffer = new ArrayList<>();

    private void logBoth(String message) {
        log.info(message);
        aiLogBuffer.add(message);
    }

    private void logError(String message, Throwable e) {
        if (e != null) log.error(message, e);
        else log.error(message);
        aiLogBuffer.add(message + (e != null ? " - " + e.getMessage() : ""));
    }

    @Test
    public void aiSeleniumTest(VerisoftDriver driver) throws Exception {
        logBoth("🚀 התחלת טסט AI אוטומטי...");
        driver.get("https://www.w3schools.com/");
        logBoth("🌐 נפתח האתר W3Schools בהצלחה.");

        // אתחול TryAI (מודל ניתן לשינוי דרך פרמטר)
        TryAI ai = new TryAI("gpt-4o-mini");

        // תקציר הדף שנשלח ל-AI
        String pageSummary = TryAI.shortenHtml(driver.getPageSource());

        // שמירת פריטים חשובים ב-tryAI logs
        ai.addLog("Opened page: https://www.w3schools.com/");
        ai.addLog("Page summary length: " + pageSummary.length());

        // בקשת קוד ראשונית מה-AI
        String userRequest = "תקליד JAVA בתיבת החיפוש שבמרכז העמוד . תיבת החיפוש נמצאת בעמוד מתחת הכותרת Learn to Code ";
        String code = ai.generateSeleniumCode(pageSummary, userRequest);
        logBoth("🤖 הקוד שה-AI יצר:\n" + code);
        ai.addLog("AI generated code:\n" + code);

        // הרצה מבוקרת
        String error = runAIActionSafely(driver, code, ai);

        if (error != null) {
            // שמור את השגיאה גם ב-AI
            ai.addLog("Error during execution: " + error);
            logError("❌ הקוד נכשל בהרצה הראשונה. שגיאה: " + error, null);

            // בקשת תיקון חכמה ל-AI כולל היסטוריה ולוגים
            String fixedCode;
            try {
                fixedCode = ai.requestFixWithContext(code, error);
            } catch (Exception exc) {
                logError("Failed to request fix from AI: " + exc.getMessage(), exc);
                throw exc;
            }

            logBoth("🔁 הקוד המתוקן שה-AI יצר:\n" + fixedCode);
            ai.addLog("AI fixed code:\n" + fixedCode);

            // נסיון נוסף להריץ את הקוד המתוקן
            String secondError = runAIActionSafely(driver, fixedCode, ai);
            if (secondError != null) {
                ai.addLog("Second attempt failed: " + secondError);
                logError("Second attempt failed: " + secondError, null);
            }
        }

        // בסיום — שלח את כל הלוגים ל-AI (ניתוח סופי) - אופציונלי
        try {
            String analysis = ai.sendLogsToAI(); // מחזיר את תגובת ה-AI
            logBoth("AI logs analysis response:\n" + analysis);
        } catch (Exception e) {
            logError("Failed to send logs to AI: " + e.getMessage(), e);
        }

        // במידה ורוצים — נסיים עם שימור הלוגים בקובץ או db (לא בגדר זה כרגע)
        Thread.sleep(2000);
    }

    // נוסיף TryAI כפרמטר רק כדי שהשיטות יוכלו להוסיף לוגים פנימיים
    private String runAIActionSafely(VerisoftDriver driver, String code, TryAI ai) {
        try {
            logBoth("▶️ התחלת הרצת קוד ה-AI...");
            ai.addLog("Executing code:\n" + code);
            DynamicExecutor.execute(driver, code);
            logBoth("✅ הקוד של ה-AI הורץ בהצלחה!");
            ai.addLog("Execution success");
            return null;
        } catch (Exception e) {
            logError("❌ שגיאה במהלך הרצת הקוד של ה-AI", e);
            ai.addLog("Execution exception: " + e.toString());
            return cleanErrorMessage(e);
        }
    }

    private String cleanErrorMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null) msg = e.toString();
        if (msg.contains("NoSuchElementException"))
            return "NoSuchElementException – לא נמצא האלמנט שביקשת לחפש.";
        if (msg.contains("TimeoutException"))
            return "TimeoutException – כנראה חיכית יותר מדי זמן לאלמנט שלא נטען.";
        return msg;
    }
}
