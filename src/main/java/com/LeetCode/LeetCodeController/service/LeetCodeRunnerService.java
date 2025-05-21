package com.LeetCode.LeetCodeController.service;

import com.LeetCode.LeetCodeController.model.LeetCodeRequest;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;

@Service
public class LeetCodeRunnerService {

    private static final String JAVA_CLASS_PATH = "src/main/java/com/LeetCode/LeetCodeController/exercises/";
    private static final String LEETCODE_URL = "https://leetcode.com/";
    private static final String IFRAME_XPATH = "//iframe[contains(@src,'/playground/UpwhGDg6/shared')]";
    private static final String CODEMIRROR_CONTAINER = "//div[@id='app']";
    private static final String RUN_BUTTON_XPATH = "//button[contains(@class,'run-code-btn')]";
    private static final String RESULT_XPATH = "//div[@id='output-console']";

    public String runCode(LeetCodeRequest request) {
        String code;
        try {
            code = loadJavaFileContent(request.getClassName());
            System.out.println("Injected code:\n" + code);
        } catch (IOException e) {
            return "Error reading Java class file: " + e.getMessage();
        }

        WebDriver driver = new EdgeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        String result;

        try {
            driver.get(LEETCODE_URL);
            driver.manage().window().maximize();
            Thread.sleep(2000);


            // Scroll down to make iframe visible
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);

            // Switch to iframe
            WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(IFRAME_XPATH)));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", iframe);
            driver.switchTo().frame(iframe);
            Thread.sleep(1000);

            // Ensure editor is ready
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(CODEMIRROR_CONTAINER)));
            WebElement cm = driver.findElement(By.xpath(CODEMIRROR_CONTAINER));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cm);
            Thread.sleep(1000);

            // Click the Java language button if needed
            WebElement javaButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Java']")));
            javaButton.click();
            Thread.sleep(1000);

            // First, clear the editor
            ((JavascriptExecutor) driver).executeScript("""
    const editor = document.querySelector('.CodeMirror').CodeMirror;
    editor.setValue("");
    editor.focus();
""");
            Thread.sleep(500);
            String[] lines = code.split("\n");
            for (String line : lines) {
                ((JavascriptExecutor) driver).executeScript("""
        const editor = document.querySelector('.CodeMirror').CodeMirror;
        editor.replaceSelection(arguments[0] + "\\n");
    """, line);
                Thread.sleep(50); // Shorter delay since it's per line
            }

            Thread.sleep(2000);

            //  Click Run button
            WebElement runButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(RUN_BUTTON_XPATH)));
            runButton.click();

            //  Wait for result
            WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(RESULT_XPATH)));
            Thread.sleep(5000); // Give output time to settle
            result = resultElement.getText();

        } catch (Exception e) {
            result = "Error during execution: " + e.getMessage();
        } finally {
            driver.quit();
        }
        return result;
    }

    private String loadJavaFileContent(String className) throws IOException {
        Path path = Paths.get(JAVA_CLASS_PATH + className + ".java");
        System.out.println("Loading file: " + path.toAbsolutePath()); // Debug log
        List<String> lines = Files.readAllLines(path);
        return (lines.size() <= 1) ? "" : String.join("\n", lines.subList(1, lines.size())); // skip package line
    }
}
