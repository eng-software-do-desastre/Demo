
package com.example.demo.selenide.tests;

import com.example.demo.selenide.pages.BookstorePage;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Bookstore")
@Feature("Adicionar produto")
public class BookstoreTest extends BaseSelenideTest {

    @Test
    @Story("Adicionar primeiro livro ao carrinho")
    @Description("Valida se o carrinho mostra o livro após clique no botão")
    public void adicionarLivroAoCarrinho() {
        BookstorePage page = new BookstorePage().open();
        page.addFirstBookToCart();
        String cartText = page.getCartItemText();
        assertTrue(cartText.contains("Book"), "Carrinho deve conter um livro");
    }
}
