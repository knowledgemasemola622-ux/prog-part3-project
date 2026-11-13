
package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MessageManagerTest {

    @TempDir
    Path tempDir;

    private MessageManager manager;
    private Path jsonFile;

    private final String sender = "+27821234567";

    @BeforeEach
    void setUp() throws IOException {
        manager = new MessageManager();
        jsonFile = tempDir.resolve("messages.json");
    }

    private Message createStoredMessage(
            int number, String recipient, String text) throws IOException {

        Message message = new Message(number, recipient, text);

        assertEquals(
                "Message successfully stored.",
                message.storeMessage(jsonFile)
        );

        manager.recordMessage(message, sender, jsonFile);

        return message;
    }

    @Test
    void testStoredMessagesAreAddedToArray() throws IOException {

        createStoredMessage(
                1,
                "+27834557896",
                "Did you get the cake?"
        );

        assertEquals(1, manager.getStoredMessages().size());
        assertEquals(
                "Did you get the cake?",
                manager.getStoredMessages().get(0)
        );
    }

    @Test
    void testLongestStoredMessage() throws IOException {

        createStoredMessage(
                1,
                "+27838884567",
                "Where are you? You are late! I have asked you to be on time."
        );

        createStoredMessage(
                2,
                "+27838884567",
                "Ok, I am leaving without you."
        );

        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                manager.getLongestStoredMessage()
        );
    }

    @Test
    void testSearchMessageByID() throws IOException {

        Message message = createStoredMessage(
                1,
                "+27834557896",
                "Did you get the cake?"
        );

        String result = manager.searchMessageByID(
                message.getMessageID()
        );

        assertTrue(result.contains("+27834557896"));
        assertTrue(result.contains("Did you get the cake?"));
    }

    @Test
    void testSearchStoredMessagesByRecipient() throws IOException {

        createStoredMessage(
                1,
                "+27838884567",
                "Where are you? You are late!"
        );

        createStoredMessage(
                2,
                "+27838884567",
                "Ok, I am leaving without you."
        );

        createStoredMessage(
                3,
                "+27834557896",
                "Did you get the cake?"
        );

        String result = manager.searchMessagesByRecipient(
                "+27838884567"
        );

        assertTrue(result.contains("Where are you? You are late!"));
        assertTrue(result.contains("Ok, I am leaving without you."));
        assertFalse(result.contains("Did you get the cake?"));
    }

    @Test
    void testDeleteStoredMessageUsingHash() throws IOException {

        Message message = createStoredMessage(
                1,
                "+27838884567",
                "Where are you? You are late!"
        );

        String result = manager.deleteMessageByHash(
                message.getMessageHash(),
                jsonFile
        );

        assertTrue(result.contains("successfully deleted"));
        assertTrue(manager.getStoredMessages().isEmpty());

        manager.loadStoredMessages(jsonFile);

        assertTrue(manager.getStoredMessages().isEmpty());
    }

    @Test
    void testStoredMessageReportContainsRequiredDetails()
            throws IOException {

        Message message = createStoredMessage(
                1,
                "+27834557896",
                "Did you get the cake?"
        );

        String report = manager.getStoredMessagesReport();

        assertTrue(report.contains(message.getMessageID()));
        assertTrue(report.contains(message.getMessageHash()));
        assertTrue(report.contains(sender));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
    }

    @Test
    void testSenderAndRecipientAreDisplayed() throws IOException {

        createStoredMessage(
                1,
                "+27834557896",
                "Did you get the cake?"
        );

        String result = manager.displayStoredSendersAndRecipients();

        assertTrue(result.contains(sender));
        assertTrue(result.contains("+27834557896"));
    }

    @Test
    void testStoredMessagesCanBeLoadedFromJson() throws IOException {

        createStoredMessage(
                1,
                "+27834557896",
                "Did you get the cake?"
        );

        MessageManager loadedManager = new MessageManager();
        loadedManager.loadStoredMessages(jsonFile);

        assertEquals(1, loadedManager.getStoredMessages().size());
        assertEquals(
                "Did you get the cake?",
                loadedManager.getStoredMessages().get(0)
        );

        String report = loadedManager.getStoredMessagesReport();
        assertTrue(report.contains(sender));
    }

    
@Test
void testSentMessageIsAddedToSentArray() throws IOException {

    Message message = new Message(
            1,
            "+27834557896",
            "Did you get the cake?"
    );

    assertEquals(
            "Message successfully sent.",
            message.SentMessage()
    );

    manager.recordMessage(message, sender, jsonFile);

    assertTrue(
            manager.getSentMessages().contains("Did you get the cake?")
    );
}

@Test
void testDisregardedMessageIsAddedToDisregardedArray()
        throws IOException {

    Message message = new Message(
            2,
            "+27838884567",
            "Where are you?"
    );

    message.disregardMessage();

    manager.recordMessage(message, sender, jsonFile);

    assertTrue(
            manager.getDisregardedMessages().contains("Where are you?")
    );
}

}
