package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTests {

    @Test
    public void testUserEntity_SettersAndGetters() {
        // Test Default Constructor & Setters
        User user = new User();
        
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUsername("testuser");
        user.setPassword("secret");
        user.setFullname("Test User Fullname"); // Menutupi setFullname
        user.setRole("ROLE_USER");

        // Test Getters
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getFullname()).isEqualTo("Test User Fullname"); // Menutupi getFullname
        assertThat(user.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    public void testUserEntity_AllArgsConstructor() {
        // Test Constructor dengan Parameter (Menutupi Constructor Lengkap)
        String username = "admin";
        String password = "adminPass";
        String fullname = "Administrator";
        String role = "ADMIN";

        User user = new User(username, password, fullname, role);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getFullname()).isEqualTo(fullname);
        assertThat(user.getRole()).isEqualTo(role);
    }
}