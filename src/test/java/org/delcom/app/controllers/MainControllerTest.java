package org.delcom.app.controllers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    // Karena MainController tidak punya dependency (@Autowired), 
    // kita bisa langsung buat objectnya dengan 'new'.
    private final MainController mainController = new MainController();

    // --- 1. Test URL Root ("/") ---
    @Test
    void testRoot() {
        // Panggil method root()
        String view = mainController.root();
        
        // Pastikan return value sesuai dengan kode controller
        assertEquals("redirect:/dashboard", view);
    }

    // --- 2. Test URL Admin ("/admin") ---
    @Test
    void testAdmin() {
        // Panggil method admin()
        String response = mainController.admin();
        
        // Pastikan return value sesuai dengan string yang di-return
        assertEquals("Welcome Admin", response);
    }
}