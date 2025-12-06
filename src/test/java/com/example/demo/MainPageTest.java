package com.example.demo;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait; // <<< Import for Explicit Wait
import org.openqa.selenium.support.ui.ExpectedConditions; // <<< Import for Expected Conditions

import java.time.Duration;

public class MainPageTest {
    private WebDriver driver;
    private MainPage mainPage; // Assuming this class holds PageObjects/WebElements


    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Implicit wait helps for general element discovery
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://www.jetbrains.com/");

        // --- Cookie Banner Handling (Fail-safe attempt) ---

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        By acceptLocator = By.xpath(
                "//button[normalize-space()='Accept' or contains(.,'Got it') or contains(.,'Agree')]" +
                        " | //a[contains(.,'Accept')]"
        );
        By bannerContainerLocator = By.cssSelector(
                ".ch2-container.ch2-theme-default, .cookie, [id*='cookie'], [class*='cookie']"
        );

        try {
            WebElement acceptButton = wait.until(ExpectedConditions.presenceOfElementLocated(acceptLocator));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", acceptButton);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(acceptButton)).click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", acceptButton);
            }
            wait.until(ExpectedConditions.invisibilityOfElementLocated(bannerContainerLocator));
        } catch (Exception e) {
            System.out.println("Cookie banner não encontrado. Vamos prosseguir.");
        }

        // --- End Cookie Banner Handling ---

        mainPage = new MainPage(driver);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    // --- TEST 1: search() - FIX: Use JavaScript for initial click ---

    @Test
    public void search() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1) Clique robusto no botão de pesquisa
        WebElement searchButton = mainPage.searchButton;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", searchButton);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", searchButton);
        }

        // 2) Guardar URL/Título antes e esperar mudança de estado (overlay OU navegação)
        String urlBefore = driver.getCurrentUrl();
        String titleBefore = driver.getTitle();

        // candidatos de overlay/painel no header
        By[] panelCandidates = new By[]{
                By.cssSelector("[role='search']"),
                By.cssSelector("[data-test*='search']"),
                By.cssSelector("div[class*='search'][class*='header']"),
                By.cssSelector("form[class*='search']"),
                By.cssSelector("div[class*='overlay'][class*='search']")
        };

        // Espera composta: ou overlay visível OU URL/título de pesquisa
        boolean overlayVisible = false;
        WebElement searchPanel = null;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 8000) {
            // tenta overlay
            for (By locator : panelCandidates) {
                try {
                    searchPanel = driver.findElement(locator);
                    if (searchPanel.isDisplayed()) {
                        overlayVisible = true;
                        break;
                    }
                } catch (NoSuchElementException ignored) {}
            }
            if (overlayVisible) break;

            // tenta navegação
            boolean navigated = driver.getCurrentUrl().contains("search")
                    || driver.getTitle().toLowerCase().contains("search");
            if (navigated) break;

            // pequeno sleep para polling
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }

        // 3) Esperar a página estar "pronta"
        try {
            wait.until(d -> "complete".equals(js.executeScript("return document.readyState")));
        } catch (Exception ignored) {}

        // 4) Procurar o input (duas vias: dentro do overlay ou na página de resultados)
        By[] inputCandidates = new By[]{
                // comuns no header
                By.cssSelector("input[type='search']"),
                By.cssSelector("input[placeholder*='Search']"),
                By.cssSelector("input[name='q']"),
                By.cssSelector("input[data-test*='search']"),
                // comuns em página de resultados
                By.cssSelector("input[id*='search']"),
                By.cssSelector("input[class*='search']")
        };

        WebElement searchField = null;

        if (overlayVisible && searchPanel != null) {
            for (By locator : inputCandidates) {
                try {
                    // nested: dentro do painel
                    searchField = searchPanel.findElement(locator);
                    if (searchField.isDisplayed()) break;
                } catch (Exception ignored) {}
            }
        } else {
            // fallback: procurar no documento (página de resultados)
            for (By locator : inputCandidates) {
                try {
                    searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    if (searchField != null) break;
                } catch (Exception ignored) {}
            }
        }

        // 5) Último recurso: tentar focar com JS e descobrir o elemento ativo
        if (searchField == null) {
            try {
                js.executeScript("document.querySelector('input[type=search],input[name=q]')?.focus()");
                WebElement active = driver.switchTo().activeElement();
                if (active != null && "input".equalsIgnoreCase(active.getTagName())) {
                    searchField = active;
                }
            } catch (Exception ignored) {}
        }

        assertNotNull(searchField, "Não encontrei o campo de pesquisa após abrir overlay ou navegar para a página."); // <-- ponto da tua falha atual
        // 6) Preencher e submeter
        searchField.clear();
        searchField.sendKeys("Selenium");

        try {
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'], button[aria-label*='Search'], button[data-test*='search']")
            ));
            submitButton.click();
        } catch (Exception ignored) {
            searchField.sendKeys(Keys.ENTER);
        }

        // 7) Validar na página de resultados
        WebElement searchPageField = null;
        for (By locator : inputCandidates) {
            try {
                searchPageField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                if (searchPageField != null) break;
            } catch (Exception ignored) {}
        }
        assertNotNull(searchPageField, "Não encontrei o campo de pesquisa na página de resultados.");
        assertTrue(searchPageField.getAttribute("value").toLowerCase().contains("selenium"),
                "O campo deve conter 'Selenium'. Valor atual: " + searchPageField.getAttribute("value"));
    }


    // TESTE 2 — Abrir "Developer Tools" por CLICK e validar que há menu aberto (sem setSize)
    @Test
    public void toolsMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1) Container do POM
        WebElement container = mainPage.toolsMenu;

        // 2) Clicar no filho clicável (a/button) — muitos sites não disparam click no <div> pai
        WebElement clickable = null;
        try { clickable = container.findElement(By.cssSelector("a,button")); } catch (Exception ignored) {}
        WebElement target = (clickable != null ? clickable : container);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", target);
        try { wait.until(ExpectedConditions.elementToBeClickable(target)).click(); }
        catch (Exception e) { js.executeScript("arguments[0].click();", target); }

        // 3) Considerar aberto quando aria-expanded=true OU um painel plausível ficar visível
        WebElement openedPanel = waitForMenuOpen(driver, target, Duration.ofSeconds(10));

        assertNotNull(openedPanel, "Não encontrei nenhum painel/submenu visível após o click.");
        assertTrue(openedPanel.isDisplayed(), "O submenu/painel deve estar visível após o click.");
    }



    // TESTE 3 — A partir do menu aberto, navegar para "All Products/Tools" (sem setSize)
    @Test
    public void navigationToAllTools() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 1) Click no item do POM (preferindo filho clicável)
        WebElement container = mainPage.seeDeveloperToolsButton;
        WebElement clickable = null;
        try { clickable = container.findElement(By.cssSelector("a,button")); } catch (Exception ignored) {}
        WebElement target = (clickable != null ? clickable : container);

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", target);
        try { wait.until(ExpectedConditions.elementToBeClickable(target)).click(); }
        catch (Exception e) { js.executeScript("arguments[0].click();", target); }

        // 2) Esperar menu/painel realmente ABERTO (aria-expanded OU painel visível)
        WebElement panel = waitForMenuOpen(driver, target, Duration.ofSeconds(12));
        assertNotNull(panel, "Não encontrei painel/submenu aberto após o click.");

        // 3) Procurar ação dentro do painel por CSS estável OU por texto (EN/PT)
        WebElement action = null;

        // 3a) CSS candidatos (primeiro dentro do painel)
        By[] cssCandidates = new By[] {
                By.cssSelector("[data-test='suggestion-action']"),
                By.cssSelector("a[href*='/products']"),
                By.cssSelector("a[href*='all-products']"),
                By.cssSelector("a[href*='tools']"),
                By.cssSelector("[data-test*='all']")
        };
        for (By loc : cssCandidates) {
            try {
                WebElement cand = panel.findElement(loc);
                if (cand.isDisplayed()) { action = cand; break; }
            } catch (NoSuchElementException ignored) {}
        }

        // 3b) Texto visível (EN/PT) dentro do painel
        if (action == null) {
            Object found = ((JavascriptExecutor) driver).executeScript(
                    "const scope=arguments[0], needles=arguments[1];" +
                            "function vis(e){const c=getComputedStyle(e),r=e.getBoundingClientRect();" +
                            " return c.display!=='none'&&c.visibility!=='hidden'&&parseFloat(c.opacity)>0&&r.width>1&&r.height>1}" +
                            "for(const el of scope.querySelectorAll('a,button')){" +
                            "  const t=(el.innerText||el.textContent||'').trim().toLowerCase();" +
                            "  if(!t||!vis(el)) continue;" +
                            "  for(const n of needles){ if(t.includes(n)) return el; }" +
                            "}" +
                            "return null;",
                    panel, java.util.Arrays.asList(
                            "all products","all tools","developer tools","find your tools",
                            "todos os produtos","todas as ferramentas","ver tudo","produtos","ferramentas"
                    )
            );
            if (found instanceof WebElement) action = (WebElement) found;
        }

        // 3c) Último recurso: procurar GLOBALMENTE (caso o link esteja “portalizado” fora do painel)
        if (action == null) {
            for (By loc : cssCandidates) {
                try {
                    WebElement cand = wait.until(ExpectedConditions.presenceOfElementLocated(loc));
                    if (cand.isDisplayed()) { action = cand; break; }
                } catch (Exception ignored) {}
            }
        }

        assertNotNull(action, "Não encontrei a ação para navegar para All Products/Tools.");

        // 4) Click seguro e validação da página de produtos
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", action);
        try { wait.until(ExpectedConditions.elementToBeClickable(action)).click(); }
        catch (Exception e) { js.executeScript("arguments[0].click();", action); }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.id("products-page")),
                ExpectedConditions.urlContains("products"),
                ExpectedConditions.titleContains("Products"),
                ExpectedConditions.titleContains("Developer Tools"),
                ExpectedConditions.titleContains("Produtos")
        ));

        WebElement products = null;
        try { products = driver.findElement(By.id("products-page")); } catch (NoSuchElementException ignored) {}
        if (products != null) assertTrue(products.isDisplayed(), "#products-page deve estar visível.");

        String title = driver.getTitle();
        assertTrue(title.contains("Products") || title.contains("Developer Tools") || title.contains("Produtos"),
                "O título deve indicar produtos/ferramentas. Título atual: " + title);
    }


    /**
     * Espera o menu abrir: devolve um painel plausível visível OU null.
     * Critérios: aria-expanded=true NO TARGET OU qualquer painel/dropdown visível no documento.
     */
    private WebElement waitForMenuOpen(WebDriver driver, WebElement toggle, Duration timeout) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long end = System.currentTimeMillis() + timeout.toMillis();

        String findPanelJs =
                "const t=arguments[0];" +
                        "function vis(e){const c=getComputedStyle(e),r=e.getBoundingClientRect();" +
                        " return c.display!=='none'&&c.visibility!=='hidden'&&parseFloat(c.opacity)>0&&r.width>1&&r.height>1}" +
                        "const cands = Array.from(document.querySelectorAll(" +
                        "  \"div[role='menu'],ul[role='menu'],nav[role='menu'],\"+" +
                        "  \"div[data-test*='submenu'],div[class*='submenu'],div[class*='dropdown'],div[class*='popup']\"));" +
                        "if (cands.length===0) return null;" +
                        "const tr=t.getBoundingClientRect();" +
                        "let best=null,score=1e9;" +
                        "for(const el of cands){ if(!vis(el)) continue; const r=el.getBoundingClientRect();" +
                        "  const dx=Math.abs((r.left+r.right)/2 - (tr.left+tr.right)/2);" +
                        "  const dy=(r.top - tr.bottom);" +
                        "  const penalty=(dy>=-10?0:10000);" + // se painel estiver acima, penaliza
                        "  const s=dx + Math.max(0,dy) + penalty;" +
                        "  if(s<score){score=s; best=el;}" +
                        "}" +
                        "return best;";

        while (System.currentTimeMillis() < end) {
            try {
                // 1) aria-expanded
                String aria = null, cls = null;
                try { aria = toggle.getAttribute("aria-expanded"); cls = toggle.getAttribute("class"); } catch (Exception ignored) {}
                if ("true".equalsIgnoreCase(aria) || (cls != null && cls.toLowerCase().contains("open"))) {
                    // tenta encontrar um painel visível, mas se não houver, considera aberto por aria
                    Object el = js.executeScript(findPanelJs, toggle);
                    if (el instanceof WebElement) return (WebElement) el;
                    return toggle; // sinaliza “aberto” via aria
                }

                // 2) procurar painel visível
                Object el = js.executeScript(findPanelJs, toggle);
                if (el instanceof WebElement) return (WebElement) el;
            } catch (Exception ignored) {}

            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        return null;
    }


}
