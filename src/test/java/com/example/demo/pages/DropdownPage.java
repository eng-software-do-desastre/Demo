
package com.example.demo.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage {
    private final WebDriver driver;
    private static final String URL = "https://the-internet.herokuapp.com/dropdown";

    public DropdownPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public DropdownPage open() {
        driver.get(URL);
        return this;
    }

    private Select dropdown() {
        // O <select> tem id="dropdown"
        WebElement select = driver.findElement(By.id("dropdown"));
        return new Select(select);
    }

    public void selectByVisibleText(String text) {
        dropdown().selectByVisibleText(text);
    }

    public String getSelectedText() {
        return dropdown().getFirstSelectedOption().getText().trim();
    }
}
