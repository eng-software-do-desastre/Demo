
package com.example.demo.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CheckboxesPage {
    private final WebDriver driver;
    private static final String URL = "https://the-internet.herokuapp.com/checkboxes";

    public CheckboxesPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public CheckboxesPage open() {
        driver.get(URL);
        return this;
    }

    public List<WebElement> checkboxes() {
        // Na página, os checkboxes estão dentro de <form id="checkboxes">
        return driver.findElements(By.cssSelector("form#checkboxes input[type='checkbox']"));
    }

    public boolean isChecked(int index) {
        return checkboxes().get(index).isSelected();
    }

    public void setChecked(int index, boolean checked) {
        WebElement cb = checkboxes().get(index);
        if (cb.isSelected() != checked) {
            cb.click();
        }
    }
}
