
package com.example.demo.selenide.tests;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseSelenideTest {
    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 6000; // 6s
    }
}
