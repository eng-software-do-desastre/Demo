
package com.example.demo.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;

public class FileUploadPage {
    private final WebDriver driver;
    private static final String URL = "https://the-internet.herokuapp.com/upload";

    public FileUploadPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public FileUploadPage open() {
        driver.get(URL);
        return this;
    }

    public void chooseFile(String absolutePath) {
        // input file tem id="file-upload"
        WebElement fileInput = driver.findElement(By.id("file-upload"));
        fileInput.sendKeys(absolutePath); // requer caminho absoluto
    }

    public void submit() {
        driver.findElement(By.id("file-submit")).click();
    }

    public String uploadedFileName() {
        // depois do upload, o nome surge em #uploaded-files
        return driver.findElement(By.id("uploaded-files")).getText().trim();
    }
}
