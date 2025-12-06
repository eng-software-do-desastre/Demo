package com.example.demo.selenide.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.example.demo.selenide.pages.SamplerPage;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class SamplerTest {
    SamplerPage page = new SamplerPage();

    @BeforeAll
    static void setupAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());

        // --- MANTENDO O CHROME ---
        Configuration.browser = "chrome";

        // Tenta adicionar permissões extra para o Chrome v142 não bloquear a conexão
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        Configuration.browserCapabilities = options;

        Configuration.headless = false;
        Configuration.timeout = 20000; // 20 segundos de espera
        Configuration.browserSize = "1920x1080";
        Configuration.holdBrowserOpen = true; // Mantém aberto no fim
    }

    @BeforeEach
    void setUp() { page.openPage(); }

    @Test
    void testInteraction() throws InterruptedException {
        System.out.println("Encontrei " + page.findClickable() +  " tipos de objetos.");

        page.clickFirstOption();
        Thread.sleep(2000);
        page.clickButton();
    }

}
