
package quickchat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    Login login = new Login("Tuduetso", "Mmokwa");

    @Test
    void usernameWithUnderscoreAndFiveCharactersIsValid() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void usernameWithoutUnderscoreIsInvalid() {
        assertFalse(login.checkUserName("kyle1"));
    }

    @Test
    void passwordWithRequiredCharactersIsValid() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void passwordWithoutRequiredComplexityIsInvalid() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    void southAfricanCellNumberWithInternationalCodeIsValid() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void cellNumberWithoutInternationalCodeIsInvalid() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    void registeredUserCanLoginSuccessfully() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void incorrectPasswordPreventsLogin() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertFalse(login.loginUser("kyl_1", "wrongPassword1!"));
    }

    @Test
    void incorrectUsernamePreventsLogin() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertFalse(login.loginUser("wrong_1", "Ch&&sec@ke99!"));
    }

    @Test
    void successfulLoginReturnsWelcomeMessage() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        login.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertEquals(
                "Welcome Tuduetso, Mmokwa it is great to see you again.",
                login.returnLoginStatus()
        );
    }

     @Test
    void validUserCanRegister() {
        String result = login.registerUser(
                "kyl_1", "Ch&&sec@ke99!", "+27838968976");

        assertEquals("User registered successfully.", result);
    }

    @Test
    void invalidUsernamePreventsRegistration() {
        String result = login.registerUser(
                "kyle1", "Ch&&sec@ke99!", "+27838968976");

        assertTrue(result.contains("Username is not correctly formatted"));
    }

    @Test
    void weakPasswordPreventsRegistration() {
        String result = login.registerUser(
                "kyl_1", "password", "+27838968976");

        assertTrue(result.contains("Password is not correctly formatted"));
    }

    @Test
    void invalidCellNumberPreventsRegistration() {
        String result = login.registerUser(
                "kyl_1", "Ch&&sec@ke99!", "08966553");

        assertTrue(result.contains("Cell number is incorrectly formatted"));
    }
}
