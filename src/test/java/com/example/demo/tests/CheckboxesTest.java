
package com.example.demo.tests;

import com.example.demo.pages.CheckboxesPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CheckboxesTest extends BaseTest {

    @Test
    void marcarEDesmarcarCheckboxes() {
        CheckboxesPage page = new CheckboxesPage(driver).open();

        // Garante estados controlados: idx 0 -> checked, idx 1 -> unchecked
        page.setChecked(0, true);
        page.setChecked(1, false);

        assertTrue(page.isChecked(0), "Checkbox 1 devia estar selecionado");
        assertFalse(page.isChecked(1), "Checkbox 2 devia estar desmarcado");

        // Alternar estados
        page.setChecked(0, false);
        page.setChecked(1, true);

        assertFalse(page.isChecked(0), "Checkbox 1 devia estar desmarcado");
        assertTrue(page.isChecked(1), "Checkbox 2 devia estar selecionado");
    }
}
