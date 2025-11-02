package config;

import org.openqa.selenium.WebDriver;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicExecutor {

    public static void execute(WebDriver driver, String seleniumCode) throws Exception {
        String className = "GeneratedAIAction";
        String source = """
                import org.openqa.selenium.*;
                import org.openqa.selenium.support.ui.ExpectedConditions;
                import org.openqa.selenium.support.ui.WebDriverWait;
                import java.time.Duration;
            public class %s {
                public static void run(WebDriver driver) throws Exception {
                    %s
                }
            }
            """.formatted(className, seleniumCode);

        JavaFileObject file = new SimpleJavaFileObject(
                URI.create("string:///" + className + ".java"),
                JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };

        // קומפילציה לזיכרון
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("לא נמצא קומפיילר — ודאי שאת מריצה ב-JDK ולא ב-JRE בלבד!");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);

        JavaFileManager fileManager = new ForwardingJavaFileManager<>(stdFileManager) {
            private final Map<String, ByteArrayOutputStream> compiledBytes = new HashMap<>();

            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                       JavaFileObject.Kind kind, FileObject sibling) {
                return new SimpleJavaFileObject(URI.create("mem:///" + className + ".class"), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compiledBytes.put(className, baos);
                        return baos;
                    }
                };
            }

            @Override
            public ClassLoader getClassLoader(Location location) {
                return new ClassLoader(getClass().getClassLoader()) {
                    @Override
                    protected Class<?> findClass(String name) throws ClassNotFoundException {
                        ByteArrayOutputStream baos = compiledBytes.get(name);
                        if (baos == null) throw new ClassNotFoundException(name);
                        byte[] bytes = baos.toByteArray();
                        return defineClass(name, bytes, 0, bytes.length);
                    }
                };
            }
        };

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                null, null, List.of(file));

        boolean success = task.call();
        if (!success) {
            StringBuilder errors = new StringBuilder("שגיאה בקומפילציה:\n");
            for (Diagnostic<?> d : diagnostics.getDiagnostics()) {
                errors.append(d.toString()).append("\n");
            }
            throw new RuntimeException(errors.toString());
        }

        // טוענים את הקלאס מתוך ה-ClassLoader הפנימי
        ClassLoader loader = fileManager.getClassLoader(StandardLocation.CLASS_OUTPUT);
        Class<?> clazz = loader.loadClass(className);
        Method runMethod = clazz.getMethod("run", WebDriver.class);

        runMethod.invoke(null, driver);
    }
}