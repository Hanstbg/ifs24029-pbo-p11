package org.delcom.app.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFormTests {

    // --- TEST 1: COVERAGE UTAMA (Getter & Setter) ---
    // Ini yang akan mengubah warna merah menjadi hijau di JaCoCo
    @Test
    void testGettersAndSetters() {
        // 1. Instantiate (Panggil Constructor)
        RegisterForm form = new RegisterForm();

        // 2. Data Dummy
        String username = "hans";
        String password = "password123";

        // 3. Panggil Setter (Coverage Setter)
        form.setUsername(username);
        form.setPassword(password);

        // 4. Panggil Getter & Assert (Coverage Getter)
        assertEquals(username, form.getUsername());
        assertEquals(password, form.getPassword());
    }

    // --- TEST 2: VALIDASI (Opsional / Bonus) ---
    // Memastikan anotasi @Size dan @NotBlank berfungsi
    @Test
    void testValidationLogic() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Skenario: Data Invalid (Password kependekan)
        RegisterForm invalidForm = new RegisterForm();
        invalidForm.setUsername(""); // Kosong
        invalidForm.setPassword("123"); // Kurang dari 6

        var violations = validator.validate(invalidForm);
        
        // Harusnya ada error
        assertFalse(violations.isEmpty());
    }
}