package org.delcom.app.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstUtilTest {

    // --- 1. Test Nilai Konstanta ---
    // Memastikan tidak ada typo pada nilai string
    @Test
    void testConstantValues() {
        assertEquals("AUTH_TOKEN", ConstUtil.KEY_AUTH_TOKEN);
        assertEquals("USER_ID", ConstUtil.KEY_USER_ID);
        
        assertEquals("pages/auth/login", ConstUtil.TEMPLATE_PAGES_AUTH_LOGIN);
        assertEquals("pages/auth/register", ConstUtil.TEMPLATE_PAGES_AUTH_REGISTER);
        assertEquals("pages/home", ConstUtil.TEMPLATE_PAGES_HOME);
        
        assertEquals("pages/todos/home", ConstUtil.TEMPLATE_PAGES_TODOS_HOME);
        assertEquals("pages/cashflows/home", ConstUtil.TEMPLATE_PAGES_CASHFLOWS_HOME);
        assertEquals("pages/todos/detail", ConstUtil.TEMPLATE_PAGES_TODOS_DETAIL);
    }

    // --- 2. Test Constructor (PENTING UNTUK COVERAGE) ---
    // JaCoCo menghitung default constructor sebagai "code".
    // Kita harus memanggilnya agar class coverage menjadi 100%.
    @Test
    void testConstructor() {
        ConstUtil constUtil = new ConstUtil();
        assertNotNull(constUtil);
    }
}