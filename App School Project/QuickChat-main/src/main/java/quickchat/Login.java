
package quickchat;

import java.util.Objects;

public class Login {

    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellNumber;
    private final String firstName;
    private final String lastName;
    private boolean loginSuccessful;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        return password != null
                && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    }

    public boolean checkCellPhoneNumber(String cellNumber) {
        return cellNumber != null
                && cellNumber.matches("^\\+27\\d{9}$");
    }

    public String registerUser(String username, String password, String cellNumber) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredCellNumber = cellNumber;

        return "User registered successfully.";
    }

    public boolean loginUser(String username, String password) {
        loginSuccessful = registeredUsername != null
                && Objects.equals(registeredUsername, username)
                && Objects.equals(registeredPassword, password);

        return loginSuccessful;
    }

    public String returnLoginStatus() {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }

    // Returns the cellphone number of the registered user.
    public String getRegisteredCellNumber() {
        return registeredCellNumber;
    }
}

