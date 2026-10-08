package pe.edu.upeu.ms_auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthSeedDataTest {

    private static final String ADMIN_BCRYPT_HASH =
            "$2b$10$iFyuKgtnbdtpq4zFW59cQeNRhF33J90qsgvIYCZRI2SP/QWridn1.";

    @Test
    void seedAdminPasswordMatchesAdmin123() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("admin123", ADMIN_BCRYPT_HASH));
    }
}
