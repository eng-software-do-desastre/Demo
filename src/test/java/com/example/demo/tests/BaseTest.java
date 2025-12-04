
package com.example.demo.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeEach
    void setUp() {
        // Selenium Manager resolve o ChromeDriver automaticamente no Selenium 4
        driver = new ChromeDriver();
        // driver.manage().window().maximize(); // opcional; remove se preferires exatamente como está
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // só explicit waits nos testes
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
