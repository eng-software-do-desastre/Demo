package com.example.demo.selenide.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;

public class DatabasePage {

    private static final String URL = "https://vaadin-database-example.demo.vaadin.com/";

    // Mapeamos a Grid (Tabela)
    private final SelenideElement grid = $("vaadin-grid");

    public void openPage() {
        open(URL);
        // Espera que a tabela esteja visível
        grid.shouldBe(visible);
    }

    public int getRowCount() {
        // ESTRATÉGIA ROBUSTA:
        // Usa JavaScript para perguntar diretamente ao componente Vaadin quantas linhas tem.
        // Isto funciona mesmo se o HTML estiver complexo.
        try {
            Long size = executeJavaScript("return document.querySelector('vaadin-grid').items.length;");
            return size != null ? size.intValue() : 0;
        } catch (Exception e) {
            // Fallback: Se o JS falhar, conta as linhas visíveis no DOM
            return $$("vaadin-grid-row").size();
        }
    }

    public void clickFirstRow() {
        // Clica na primeira célula de conteúdo que encontrar para selecionar a linha
        // "vaadin-grid-cell-content" é a tag interna onde o texto está
        if ($$("vaadin-grid-cell-content").size() > 0) {
            $$("vaadin-grid-cell-content").first().shouldBe(visible).click();
        } else {
            System.out.println("Aviso: A tabela parece estar vazia, não deu para clicar.");
        }
    }
}