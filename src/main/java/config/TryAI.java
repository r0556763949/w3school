package config;

import org.json.JSONArray;
import org.json.JSONObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TryAI {
    private final String apiKey;
    private final String model;
    private final HttpClient http;
    private final List<String> conversation; // שיחות/פרומפטים שנשלחו (כדי לשלוח ולבנות הקשר)
    private final List<String> responses;    // תשובות שקיבלנו מה-AI
    private final List<String> logs;         // לוגים שנצברו במהלך הריצה

    public TryAI(String model) {
       this.apiKey = loadApiKey();
        if (this.apiKey == null || this.apiKey.isBlank()) {
            throw new IllegalStateException("No API key found in OPENAI_API_KEY environment variable");
        }
        this.model = model == null ? "gpt-4o-mini" : model;
        this.http = HttpClient.newHttpClient();
        this.conversation = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.logs = new ArrayList<>();
    }

    public String generateSeleniumCode(String pageSummary, String userRequest) throws Exception {
        String fullPrompt = """
            אתה מקבל תקציר של עמוד אינטרנט (HTML מקוצר):
            ---
            %s
            ---
            המשימה: %s

            כתוב **רק** קוד Java שמשתמש ב-Selenium WebDriver (בצורת פקודות על driver).
            החזרת הקוד תהיה **ללא imports** ו**ללא אתחולים**. החזר רק פקודות Selenium המתייחסות ל-driver (לדוגמא: driver.findElement(By.id("q")).sendKeys("JAVA");).
            אסור לכלול System.exit, Runtime.exec, קבצי IO, יצירת sockets, או כל פקודת מערכת.אתה יודע בטח איך למצוא אלמנטים בתוך HTML הכי עדיף ID ואם לא אז באמצעות NAME או שתבנה XPATH טוב.
            """.formatted(pageSummary, userRequest);

        conversation.add("USER_PROMPT:" + Instant.now() + "\n" + fullPrompt);
        String raw = sendToAI(fullPrompt);
        responses.add(raw);
        String code = extractCodeFromResponse(raw);
        return sanitizeAndValidateGeneratedCode(code);
    }

    // שולח ל-AI את כל השיחה + הלוגים + בקשת תיקון ומחזיר את הקוד המתוקן
    public String requestFixWithContext(String failingCode, String errorSummary) throws Exception {
        String history = buildConversationHistory();
        String logsText = buildLogsText();
        String prompt = """
            קוד שנשלח קודם נכשל כאשר רצתה השורה/הפעולה הבאה:

            הכנס כאן את הקוד שנכשל:
            ---
            %s
            ---

            תקציר השגיאה:
            ---
            %s
            ---

            היסטוריית השיחה עד כה:
            %s

            הלוגים מההרצה:
            %s

            מדריך:
            - תקן את הקוד כך שימצא את האלמנט בצורה מהימנה (השתמש ב-By.* או ב-waits).
            - החזר רק את הבלוק של הקוד (ללא imports/אתחולים).
            - היזהר לא להפעיל פקודות מערכת מסוכנות.
            - אם אי אפשר למצוא אלמנט — תציע alternative selector או בדיקת זמן טעינה.

            החזר רק הקוד המתוקן (אין להסביר).
            """.formatted(failingCode, errorSummary, history, logsText);

        conversation.add("REQUEST_FIX:" + Instant.now() + "\n" + prompt);
        String raw = sendToAI(prompt);
        responses.add(raw);
        String fixedCode = extractCodeFromResponse(raw);
        return sanitizeAndValidateGeneratedCode(fixedCode);
    }

    // מאפשר להוסיף לוגים מתוך הטסט (AITest ישלח לכאן כל שורה)
    public void addLog(String message) {
        logs.add(Instant.now() + " - " + message);
    }

    // שולח ל-AI רק את הלוגים לצורך ניתוח (אם תרצי)
    public String sendLogsToAI() throws Exception {
        String logsText = buildLogsText();
        String prompt = """
            הנה הלוגים מהריצה:
            %s

            אנא נתח וספק הצעה לתיקון אם יש בעיה (קצר).
            """.formatted(logsText);

        conversation.add("SEND_LOGS:" + Instant.now() + "\n" + logsText);
        String raw = sendToAI(prompt);
        responses.add(raw);
        return raw;
    }

    private String buildConversationHistory() {
        return conversation.stream().collect(Collectors.joining("\n\n---\n\n"));
    }

    private String buildLogsText() {
        return logs.stream().collect(Collectors.joining("\n"));
    }

    // מפענח את התשובה JSON ומוציא את ה-'content' (כמו שעשית)
    public static String extractCodeFromResponse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray choices = obj.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");
            return content.replace("```java", "").replace("```", "").trim();
        } catch (Exception e) {
            return "ERROR_PARSING_JSON: " + e.getMessage() + "\nRAW:\n" + json;
        }
    }

    // בדיקה בסיסית של ביטויים אסורים וניקוי קוד
    private String sanitizeAndValidateGeneratedCode(String code) {
        String[] forbidden = new String[]{
                "System.exit", "Runtime.getRuntime", "ProcessBuilder", "new Process", "Files.write",
                "File(", "Socket(", "ServerSocket", "Class.forName", "getRuntime().exec", "setAccessible"
        };
        for (String f : forbidden) {
            if (code.contains(f)) {
                throw new SecurityException("Generated code contains forbidden expression: " + f);
            }
        }
        // קיצוץ סביר בגודל
        if (code.length() > 20_000) code = code.substring(0, 20_000) + "\n//...[TRIMMED]";
        return code.trim();
    }

    // שליחה ל-OpenAI (Chat Completions v1 כמו בקוד שלך)
    private String sendToAI(String prompt) throws Exception {
        String safePrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        String body = """
            {
              "model": "%s",
              "messages": [{"role": "user", "content": "%s"}]
            }
            """.formatted(this.model, safePrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("AI request failed: " + response.statusCode() + " -> " + response.body());
        }
        return response.body();
    }

    // עזרים: להשיג היסטוריית שיחה או תשובות במידת הצורך
    public List<String> getConversation() { return List.copyOf(conversation); }
    public List<String> getResponses() { return List.copyOf(responses); }
    public List<String> getLogs() { return List.copyOf(logs); }
    public static String shortenHtml(String html) {
        if (html == null) return "";

        // 1️⃣ הורדת כל ה-script וה-style
        html = html.replaceAll("(?s)<script.*?>.*?</script>", "");
        html = html.replaceAll("(?s)<style.*?>.*?</style>", "");

        // 2️⃣ הורדת תגיות שאינן תורמות להבנה (metadata, comments וכו’)
        html = html.replaceAll("(?s)<!--.*?-->", "");
        html = html.replaceAll("(?i)<(meta|link|noscript|iframe|svg|path)[^>]*>.*?</\\1>", "");

        // 3️⃣ קיצור רווחים
        html = html.replaceAll("\\s+", " ").trim();

        // 4️⃣ הגבלת אורך (למשל ל־4000 תווים)
        int maxLen = 4000;
        if (html.length() > maxLen)
            html = html.substring(0, maxLen) + "... [HTML shortened]";

        return html;
    }
    private static String loadApiKey() {
        // קודם ננסה מה־ENV של מערכת ההפעלה
        String key = System.getenv("OPENAI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }

        // אם אין — ננסה לקרוא מהקובץ .env
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("OPENAI_API_KEY=")) {
                    return line.substring("OPENAI_API_KEY=".length()).trim();
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ לא נמצא קובץ .env או שגיאה בקריאה: " + e.getMessage());
        }

        throw new RuntimeException("❌ לא נמצא מפתח API ל-OpenAI! ודאי שיש משתנה סביבה או קובץ .env עם OPENAI_API_KEY.");
    }
}
