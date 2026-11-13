
package quickchat;

import java.security.SecureRandom;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class Message {

    private final String messageID;
    private final String recipient;
    private final String messageText;
    private final int messageNumber;
    private String messageHash;
    private String messageStatus;

    private static int totalMessagesSent = 0;

    private static final Path JSON_FILE = Path.of("messages.json");

    // Constructor: creates a message and generates a random 10-digit ID.
    public Message(int messageNumber, String recipient, String messageText) {

        SecureRandom random = new SecureRandom();

        this.messageID = String.valueOf(
                random.nextLong(1_000_000_000L, 10_000_000_000L)
        );

        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;

        this.messageHash = "";
        this.messageStatus = "Pending";
    }

    // Checks whether the message ID contains exactly 10 digits.
    public Boolean checkMessageID() {
        return messageID.matches("\\d{10}");
    }

    // Validates the recipient's South African cellphone number.
    // Only international format beginning with +27 is accepted.
    public String checkRecipientCell() {

        if (recipient != null && recipient.matches("^\\+27\\d{9}$")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // Checks the message length.
    public String checkMessageLength() {

        if (messageText == null) {
            return "Message cannot be empty.";
        }

        if (messageText.length() <= 250) {
            return "Message ready to send.";
        }

        int excessCharacters = messageText.length() - 250;

        return "Message exceeds 250 characters by "
                + excessCharacters
                + ", please reduce the size.";
    }

    // Creates the message hash.
   
public String createMessageHash() {

    if (messageText == null || messageText.trim().isEmpty()) {
        return "";
    }

    String[] words = messageText.trim().split("\\s+");

    String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
    String lastWord = words[words.length - 1]
            .replaceAll("[^a-zA-Z0-9]", "");

    messageHash = messageID.substring(0, 2)
            + ":" + messageNumber
            + ":" + (firstWord + lastWord).toUpperCase();

    return messageHash;
}

    // Sends the message.
   
public String SentMessage() {

    if (!checkMessageID()) {
        return "Invalid message ID.";
    }

    if (!checkRecipientCell().equals(
            "Cell phone number successfully captured.")) {
        return checkRecipientCell();
    }

    if (!checkMessageLength().equals("Message ready to send.")) {
        return checkMessageLength();
    }

    // Only count the message once.
    if (!"Sent".equals(messageStatus)) {
        createMessageHash();
        messageStatus = "Sent";
        totalMessagesSent++;
    }

    return "Message successfully sent.";
}
    // Disregards the message.
    public String disregardMessage() {

        messageStatus = "Disregarded";

        return "Press 0 to delete the message.";
    }

    // Stores the message in a JSON file.
   public String storeMessage() {
    return storeMessage(JSON_FILE);
}

public String storeMessage(Path file) {

    if (!checkMessageID()) {
        return "Invalid message ID.";
    }

    if (!checkRecipientCell().equals(
            "Cell phone number successfully captured.")) {
        return checkRecipientCell();
    }

    if (!checkMessageLength().equals("Message ready to send.")) {
        return checkMessageLength();
    }

    createMessageHash();

    messageStatus = "Stored";

        String jsonMessage = "{"
                + "\"messageID\":\"" + escapeJson(messageID) + "\","
                + "\"messageNumber\":" + messageNumber + ","
                + "\"recipient\":\"" + escapeJson(recipient) + "\","
                + "\"messageText\":\"" + escapeJson(messageText) + "\","
                + "\"messageHash\":\"" + escapeJson(messageHash) + "\","
                + "\"messageStatus\":\"" + messageStatus + "\""
                + "}";

        try {
            String existingData = "[]";

            if (Files.exists(file)) {
                existingData = Files.readString(
                        file, StandardCharsets.UTF_8
                ).trim();
            }

            if (existingData.isEmpty() || existingData.equals("[]")) {
                existingData = "[" + jsonMessage + "]";
            } else if (existingData.endsWith("]")) {
                existingData = existingData.substring(
                        0, existingData.length() - 1
                ) + "," + jsonMessage + "]";
            } else {
                return "Could not store message: existing JSON file is invalid.";
            }

            Files.writeString(
                    file,
                    existingData,
                    StandardCharsets.UTF_8
            );

            return "Message successfully stored.";

        } catch (IOException e) {
            return "Error storing message: " + e.getMessage();
        }
    }

    // Escapes special characters so the message can be saved as valid JSON.
    private String escapeJson(String value) {

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Displays the message details.
    public String printMessages() {

        return "\nMessage ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText
                + "\nStatus: " + messageStatus;
    }

    // Returns the total number of messages successfully sent.
    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Getter methods.
    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getMessageStatus() {
        return messageStatus;
    }
}
