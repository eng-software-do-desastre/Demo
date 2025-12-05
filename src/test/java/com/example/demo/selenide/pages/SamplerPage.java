package com.example.demo.selenide.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$$;

public class SamplerPage {

    private static final String URL = "https://demo.vaadin.com/sampler/";

    // Mapeamos a Grid (Tabela)
    private final SelenideElement sample = $(".samplelink");

    public void openPage() {
        open(URL);
        // Espera que a tabela esteja visível
        sample.shouldBe(visible);
    }

    public int findClickable() {
        try {
            Long size = executeJavaScript("return document.querySelector('.samplelink').length();");
            return size != null ? size.intValue() : 0;
        } catch (Exception e) {
            return $$(".samplelink").size();
        }
    }

    public void clickFirstOption() {
        if ($$(".samplelink").size() > 0) {
            $$(".samplelink").first().click();
        } else {
            System.out.println("Aviso: não aparecem opções selecionaveis.");
        }
    }

    public void clickButton() {
        if ($$(".v-button").size() > 0) {
            $$(".v-button").last().click();
            System.out.println("Fiz click ao botao exibido");
        } else {
            System.out.println("Aviso: não aparecem botões clicáveis.");
        }
    }
}
