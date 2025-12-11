package org.delcom.app.configs;

import jakarta.servlet.http.HttpSession;
import org.delcom.app.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    @Autowired
    private HttpSession session;

    // AMBIL DARI SESSION (Agar data bertahan saat pindah halaman)
    public User getAuthUser() {
        return (User) session.getAttribute("authUser");
    }

    // SIMPAN KE SESSION
    public void setAuthUser(User user) {
        session.setAttribute("authUser", user);
    }

    // CEK APAKAH LOGIN
    public boolean isAuthenticated() {
        return getAuthUser() != null;
    }
}