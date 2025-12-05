package com.example.demo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InformationTest {

    @Test
    public void informationWithElements() {

        ChromeOptions options = new ChromeOptions();
        options.setBinary("/home/erick/.cache/selenium/chrome/linux64/143.0.7499.40/chrome"); // coloque o caminho correto
        options.addArguments(
                "--headless=new",           // modo headless moderno
                "--no-sandbox",             // desativa o sandbox que trava no Linux moderno
                "--disable-dev-shm-usage",  // evita problemas de memória compartilhada
                "--disable-gpu"             // desativa GPU
        );

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        // Navigate to Url
        driver.get("https://www.selenium.dev/selenium/web/inputs.html");

        boolean isEmailVisible = driver.findElement(By.name("email_input")).isDisplayed();
        assertTrue(isEmailVisible);

        Rectangle res =  driver.findElement(By.name("range_input")).getRect();
        assertEquals(10, res.getX());

        driver.quit();
    }

}