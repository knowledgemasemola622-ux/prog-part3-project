package quickchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @TempDir
    Path tempDir;

    private Message createValidMessage() {
        return new Message(
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );
    }

    @Test
    void messageIdMustBeRandomAndContainTenDigits() {

        Message message1 = createValidMessage();
        Message message2 = createValidMessage();

        String id1 = message1.getMessageID();
        String id2 = message2.getMessageID();

        assertTrue(id1.matches("\\d{10}"));
        assertTrue(id2.matches("\\d{10}"));

        // Both IDs must be valid 10-digit numbers.
        // We do not require them to be different because random
        // generation can theoretically produce the same number.
    }

    @Test
    void recipientNumberMustBeValid() {

        Message message = createValidMessage();

        assertEquals(
                "Cell phone number successfully captured.",
                message.checkRecipientCell()
        );
    }

    @Test
    void recipientNumberMustBeRejectedWhenInvalid() {

        Message message = new Message(
                0, "0718693002", "Hello"
        );

        assertEquals(
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                message.checkRecipientCell()
        );
    }

    @Test
    void messageOf250CharactersMustBeAccepted() {

        String text = "A".repeat(250);

        Message message = new Message(
                0, "+27718693002", text
        );

        assertEquals(
                "Message ready to send.",
                message.checkMessageLength()
        );
    }

    @Test
    void messageLongerThan250CharactersMustBeRejected() {

        String text = "A".repeat(260);

        Message message = new Message(
                0, "+27718693002", text
        );

        assertEquals(
                "Message exceeds 250 characters by 10, please reduce the size.",
                message.checkMessageLength()
        );
    }

    @Test
    void messageHashMustUseTheRandomMessageId() {

        Message message = createValidMessage();

        String generatedID = message.getMessageID();

        String expectedHash = generatedID.substring(0, 2)
                + ":0:HITONIGHT";

        assertEquals(
                expectedHash,
                message.createMessageHash()
        );
    }

    @Test
    void multipleMessageHashesMustBeCreatedCorrectly() {

        Message message1 = new Message(
                0,
                "+27718693002",
                "Hi Mike, can you join us for dinner tonight?"
        );

        Message message2 = new Message(
                1,
                "+27718693002",
                "Hi Keegan, did you receive the payment?"
        );

        String expectedHash1 =
                message1.getMessageID().substring(0, 2)
                        + ":0:HITONIGHT";

        String expectedHash2 =
                message2.getMessageID().substring(0, 2)
                        + ":1:HIPAYMENT";

        assertEquals(expectedHash1, message1.createMessageHash());
        assertEquals(expectedHash2, message2.createMessageHash());
    }

    @Test
    void messageMustBeSentSuccessfully() {

        Message message = createValidMessage();

        assertEquals(
                "Message successfully sent.",
                message.SentMessage()
        );

        assertEquals("Sent", message.getMessageStatus());
        assertFalse(message.getMessageHash().isEmpty());
    }

    @Test
    void sendingSameMessageTwiceMustNotIncreaseTotalTwice() {

        Message message = createValidMessage();

        int totalBefore = message.returnTotalMessages();

        message.SentMessage();
        message.SentMessage();

        assertEquals(
                totalBefore + 1,
                message.returnTotalMessages()
        );
    }

    @Test
    void messageMustBeDisregardedSuccessfully() {

        Message message = createValidMessage();

        assertEquals(
                "Press 0 to delete the message.",
                message.disregardMessage()
        );

        assertEquals("Disregarded", message.getMessageStatus());
    }

    @Test
    void messageMustBeStoredInJsonFile() throws Exception {

        Message message = createValidMessage();

        Path jsonFile = tempDir.resolve("messages.json");

        assertEquals(
                "Message successfully stored.",
                message.storeMessage(jsonFile)
        );

        String savedContent = Files.readString(jsonFile);

        assertTrue(savedContent.contains(message.getMessageID()));
        assertTrue(savedContent.contains("HITONIGHT"));
        assertTrue(savedContent.contains("Stored"));
    }

    @Test
    void multipleMessagesMustBeAppendedToJsonFile() throws Exception {

        Path jsonFile = tempDir.resolve("messages.json");

        Message message1 = createValidMessage();

        Message message2 = new Message(
                1,
                "+27718693002",
                "Hi Keegan, did you receive the payment?"
        );

        message1.storeMessage(jsonFile);
        message2.storeMessage(jsonFile);

        String savedContent = Files.readString(jsonFile);

        assertTrue(savedContent.contains(message1.getMessageID()));
        assertTrue(savedContent.contains(message2.getMessageID()));

        assertTrue(
                savedContent.indexOf(message1.getMessageID())
                        < savedContent.indexOf(message2.getMessageID())
        );
    }

    @Test
    void printMessagesMustDisplayMessageDetailsInCorrectOrder() {

        Message message = createValidMessage();

        message.createMessageHash();

        String output = message.printMessages();

        assertTrue(output.contains("Message ID:"));
        assertTrue(output.contains("Message Hash:"));
        assertTrue(output.contains("Recipient:"));
        assertTrue(output.contains("Message:"));

        assertTrue(
                output.indexOf("Message ID:")
                        < output.indexOf("Message Hash:")
        );

        assertTrue(
                output.indexOf("Message Hash:")
                        < output.indexOf("Recipient:")
        );

        assertTrue(
                output.indexOf("Recipient:")
                        < output.indexOf("Message:")
        );
    }
}