
package com.example.demo.tests;

import com.example.demo.pages.DropdownPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DropdownTest extends BaseTest {

    @Test
    void selecionarOpcaoNoDropdown() {
        DropdownPage page = new DropdownPage(driver).open();

        page.selectByVisibleText("Option 1");
        assertEquals("Option 1", page.getSelectedText());

        page.selectByVisibleText("Option 2");
        assertEquals("Option 2", page.getSelectedText());
    }
}
