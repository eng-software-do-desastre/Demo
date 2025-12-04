
package com.example.demo.selenide.pages;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class BookstorePage {
    private static final String URL = "https://vaadin-bookstore-example.demo.vaadin.com/";

    public BookstorePage open() {
        Selenide.open(URL);
        return this;
    }

    public BookstorePage addFirstBookToCart() {
        $$("vaadin-grid-cell-content button").first().click();
        return this;
    }

    public String getCartItemText() {
        return $("div.cart").shouldBe(visible).getText();
    }
}
