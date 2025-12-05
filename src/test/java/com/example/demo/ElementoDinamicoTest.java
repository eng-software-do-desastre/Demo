package com.example.demo;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ElementoDinamicoTest {
    private WebDriver driver;
    private MainPage mainPage;


    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://www.jetbrains.com/");

        // Fecha o pop-up de cookies se existir
        try {
            WebElement acceptCookies = driver.findElement(
                    By.cssSelector("button[class='ch2-btn ch2-allow-all-btn ch2-btn-primary']")
            );
            acceptCookies.click();
        } catch (Exception ignored) {
            // Se não aparecer, continua normalmente
        }

        mainPage = new MainPage(driver);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }



    @Test
    public void replaceMenuTexts() throws InterruptedException {
        // Espera que o menu esteja carregado
        Thread.sleep(2000);

        // Lista de substituições: "texto original" -> "texto novo"
        Map<String, String> replacements = new HashMap<>();
        replacements.put("Developer Tools", "ISCTE Tools");
        replacements.put("Languages", "Cursos");
        replacements.put("Solutions", "Soluções ISCTE");
        replacements.put("Pricing", "Propinas");
        replacements.put("Support", "Apoio Estudante");

        // Corre JavaScript para alterar textos visíveis
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            js.executeScript(
                    "Array.from(document.querySelectorAll('nav a, nav button, nav span')).forEach(el => {" +
                            "  if (el.textContent.trim() === arguments[0]) {" +
                            "    el.textContent = arguments[1];" +
                            "  }" +
                            "});",
                    entry.getKey(), entry.getValue()
            );
        }

        Thread.sleep(3000);

        // Verifica que pelo menos um texto foi alterado
        WebElement updated = driver.findElement(By.xpath("//*[text()='ISCTE Tools']"));
        assertTrue(updated.isDisplayed());
    }

//    @Test
//    public void replaceLogoWithImageFile() throws InterruptedException {
//        // Localiza o SVG do logo da JetBrains
//        WebElement logoSVG = driver.findElement(By.cssSelector("svg._siteLogo__image_1lmjpxg_1"));
//
//        // Cria o HTML de uma imagem apontando para a tua pasta do projeto
//        String newImgTag = "<img src='images/ISCTE_logo.png' alt='ISCTE Logo' width='120'/>";
//
//        // Substitui o SVG pelo <img>
//        ((JavascriptExecutor) driver).executeScript(
//                "arguments[0].outerHTML = arguments[1];",
//                logoSVG, newImgTag
//        );
//
//        Thread.sleep(3000);
//
//        // Verifica se a imagem foi substituída
//        WebElement updatedLogo = driver.findElement(By.cssSelector("img[alt='ISCTE Logo']"));
//        assertTrue(updatedLogo.isDisplayed());
//    }




}
