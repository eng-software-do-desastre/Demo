package com.example.demo.selenide.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.demo.selenide.pages.DatabasePage;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest {

    DatabasePage databasePage = new DatabasePage();

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
    void setup() {
        databasePage.openPage();
    }

    @Test
    void testGridHasData() {
        // 1. Verifica quantas linhas a tabela tem
        int rowCount = databasePage.getRowCount();
        System.out.println("INFO: Encontrei " + rowCount + " clientes na tabela.");

        // 2. Tenta interagir (clicar)
        databasePage.clickFirstRow();

        // 3. O teste passa se a tabela não estiver vazia (o demo costuma ter dados)
        assertTrue(rowCount > 0, "A tabela da base de dados não devia estar vazia!");
    }
}