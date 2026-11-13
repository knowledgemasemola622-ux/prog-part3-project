
package quickchat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MessageManager {

    // The five required dynamic arrays.
    private final ArrayList<String> sentMessages = new ArrayList<>();
    private final ArrayList<String> disregardedMessages = new ArrayList<>();
    private final ArrayList<String> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();

    // Supporting arrays. All arrays use the same index for a message.
    private final ArrayList<String> senders = new ArrayList<>();
    private final ArrayList<String> recipients = new ArrayList<>();
    private final ArrayList<String> statuses = new ArrayList<>();

    private final ArrayList<String> allMessageTexts = new ArrayList<>();

    // Load messages already saved in the JSON file.
    public void loadStoredMessages(Path file) throws IOException {

        clearArrays();

        if (!Files.exists(file)) {
            return;
        }

        String json = Files.readString(file, StandardCharsets.UTF_8);

        if (json.isBlank()) {
            return;
        }

        JsonElement element = JsonParser.parseString(json);

        if (!element.isJsonArray()) {
            throw new IOException("Invalid messages JSON format.");
        }

        JsonArray messages = element.getAsJsonArray();

        for (JsonElement item : messages) {
            JsonObject object = item.getAsJsonObject();

            addRecord(
                    object.has("sender")
                            ? object.get("sender").getAsString()
                            : "Unknown",
                    getValue(object, "recipient"),
                    getValue(object, "messageText"),
                    getValue(object, "messageID"),
                    getValue(object, "messageHash"),
                    getValue(object, "messageStatus")
            );
        }
    }

    // Record a message in the parallel arrays.
    // For stored messages, also add the sender to the JSON record.
    public void recordMessage(Message message, String sender, Path file)
            throws IOException {

        String status = message.getMessageStatus();

        addRecord(
                sender,
                message.getRecipient(),
                message.getMessageText(),
                message.getMessageID(),
                message.getMessageHash(),
                status
        );

        if ("Stored".equals(status)) {
            addSenderToJson(message, sender, file);
        }
    }

    private void addRecord(
            String sender,
            String recipient,
            String text,
            String id,
            String hash,
            String status) {

        senders.add(sender);
        recipients.add(recipient);
        allMessageTexts.add(text);
        messageIDs.add(id);
        messageHashes.add(hash);
        statuses.add(status);

        sentMessages.add("Sent".equals(status) ? text : "");
        disregardedMessages.add(
                "Disregarded".equals(status) ? text : ""
        );
        storedMessages.add("Stored".equals(status) ? text : "");
    }

    private void addSenderToJson(
            Message message, String sender, Path file) throws IOException {

        if (!Files.exists(file)) {
            return;
        }

        String json = Files.readString(file, StandardCharsets.UTF_8);
        JsonArray messages = JsonParser.parseString(json).getAsJsonArray();

        for (JsonElement item : messages) {
            JsonObject object = item.getAsJsonObject();

            if (object.has("messageID")
                    && object.get("messageID").getAsString()
                    .equals(message.getMessageID())) {

                object.addProperty("sender", sender);
                break;
            }
        }

        Files.writeString(
                file,
                messages.toString(),
                StandardCharsets.UTF_8
        );
    }

    // Display the sender and recipient of every stored message.
    public String displayStoredSendersAndRecipients() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < storedMessages.size(); i++) {
            if (!storedMessages.get(i).isEmpty()) {
                result.append("Sender: ").append(senders.get(i))
                        .append("\nRecipient: ").append(recipients.get(i))
                        .append("\n\n");
            }
        }

        return result.length() == 0
                ? "No stored messages found."
                : result.toString();
    }

    // Find the longest stored message.
    public String getLongestStoredMessage() {

        String longest = "";

        for (String message : storedMessages) {
            if (message.length() > longest.length()) {
                longest = message;
            }
        }

        return longest.isEmpty()
                ? "No stored messages found."
                : longest;
    }

    // Search for a message by its ID.
    public String searchMessageByID(String id) {

        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(id)) {
                return "Sender: " + senders.get(i)
                        + "\nRecipient: " + recipients.get(i)
                        + "\nMessage: " + allMessageTexts.get(i);
            }
        }

        return "Message not found.";
    }

    // Find all stored messages for a particular recipient.
    public String searchMessagesByRecipient(String recipient) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < storedMessages.size(); i++) {
            if (!storedMessages.get(i).isEmpty()
                    && recipients.get(i).equals(recipient)) {

                result.append(storedMessages.get(i)).append("\n");
            }
        }

        return result.length() == 0
                ? "No messages found for this recipient."
                : result.toString().trim();
    }

    // Delete a stored message using its hash and update the JSON file.
    public String deleteMessageByHash(String hash, Path file)
            throws IOException {

        for (int i = 0; i < messageHashes.size(); i++) {

            if (messageHashes.get(i).equals(hash)
                    && !storedMessages.get(i).isEmpty()) {

                String deletedMessage = storedMessages.get(i);

                if (Files.exists(file)) {
                    JsonArray messages = JsonParser.parseString(
                            Files.readString(file, StandardCharsets.UTF_8)
                    ).getAsJsonArray();

                    JsonArray updatedMessages = new JsonArray();

                    for (JsonElement item : messages) {
                        JsonObject object = item.getAsJsonObject();

                        if (!object.has("messageHash")
                                || !object.get("messageHash").getAsString()
                                .equals(hash)) {
                            updatedMessages.add(item);
                        }
                    }

                    Files.writeString(
                            file,
                            updatedMessages.toString(),
                            StandardCharsets.UTF_8
                    );
                }

                removeRecord(i);

                return "Message: \"" + deletedMessage
                        + "\" successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    // Generate a full report of stored messages.
    public String getStoredMessagesReport() {

        StringBuilder report = new StringBuilder();

        for (int i = 0; i < storedMessages.size(); i++) {

            if (!storedMessages.get(i).isEmpty()) {
                report.append("\nMessage ID: ").append(messageIDs.get(i))
                        .append("\nMessage Hash: ").append(messageHashes.get(i))
                        .append("\nSender: ").append(senders.get(i))
                        .append("\nRecipient: ").append(recipients.get(i))
                        .append("\nMessage: ").append(storedMessages.get(i))
                        .append("\nStatus: ").append(statuses.get(i))
                        .append("\n-----------------------------\n");
            }
        }

        return report.length() == 0
                ? "No stored messages found."
                : report.toString();
    }

    private void removeRecord(int index) {
        senders.remove(index);
        recipients.remove(index);
        allMessageTexts.remove(index);
        messageIDs.remove(index);
        messageHashes.remove(index);
        statuses.remove(index);
        sentMessages.remove(index);
        disregardedMessages.remove(index);
        storedMessages.remove(index);
    }

    private void clearArrays() {
        senders.clear();
        recipients.clear();
        allMessageTexts.clear();
        messageIDs.clear();
        messageHashes.clear();
        statuses.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
    }

    private String getValue(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull()
                ? object.get(key).getAsString()
                : "";
    }

    public ArrayList<String> getSentMessages() {
        return sentMessages;
    }

    public ArrayList<String> getDisregardedMessages() {
        return disregardedMessages;
    }

    public ArrayList<String> getStoredMessages() {
        return storedMessages;
    }

    public ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public ArrayList<String> getMessageIDs() {
        return messageIDs;
    }
}
