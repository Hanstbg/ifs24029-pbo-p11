package org.delcom.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Matikan CSRF agar form POST dari HTML kita bisa masuk
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                // Izinkan folder static (css/js/gambar)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // Izinkan Auth Controller
                .requestMatchers("/auth/**").permitAll()
                // PENTING: Izinkan semua request lain (Dashboard dll).
                // Kenapa? Karena pengecekan login dilakukan manual di DashboardController 
                // menggunakan AuthContext, bukan menggunakan SecurityContext Spring.
                .anyRequest().permitAll()
            )
            // Matikan form login bawaan Spring Security agar AuthController kita yang bekerja
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}