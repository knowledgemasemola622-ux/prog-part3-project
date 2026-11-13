package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3 unit tests that use Test Data Messages 1-5 exactly as given in the brief.
 * The five messages are written to a JSON file and read back into the arrays
 * by MessageManager.loadStoredMessages(), so message 4 (which has no +27 code)
 * can be tested without going through the Message validation.
 */
class MessageManagerBriefDataTest {

    private static final String MSG_1 = "Did you get the cake?";
    private static final String MSG_2 =
            "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG_4 = "It is dinner time!";
    private static final String MSG_5 = "Ok, I am leaving without you.";

    private static final String HASH_2 = "10:1:WHERETIME";

    @TempDir
    Path tempDir;

    private MessageManager manager;
    private Path jsonFile;

    @BeforeEach
    void setUp() throws IOException {
        jsonFile = tempDir.resolve("messages.json");

        String json = """
                [
                {"messageID":"1000000001","messageNumber":0,"recipient":"+27834557896","messageText":"Did you get the cake?","messageHash":"10:0:DIDCAKE","messageStatus":"Sent","sender":"Developer"},
                {"messageID":"1000000002","messageNumber":1,"recipient":"+27838884567","messageText":"Where are you? You are late! I have asked you to be on time.","messageHash":"10:1:WHERETIME","messageStatus":"Stored","sender":"Developer"},
                {"messageID":"1000000003","messageNumber":2,"recipient":"+27834484567","messageText":"Yohoooo, I am at your gate.","messageHash":"10:2:YOHOOOOGATE","messageStatus":"Disregarded","sender":"Developer"},
                {"messageID":"1000000004","messageNumber":3,"recipient":"0838884567","messageText":"It is dinner time!","messageHash":"10:3:ITTIME","messageStatus":"Sent","sender":"Developer"},
                {"messageID":"1000000005","messageNumber":4,"recipient":"+27838884567","messageText":"Ok, I am leaving without you.","messageHash":"10:4:OKYOU","messageStatus":"Stored","sender":"Developer"}
                ]
                """;
        Files.writeString(jsonFile, json);

        manager = new MessageManager();
        manager.loadStoredMessages(jsonFile);
    }

    @Test
    void sentMessagesArrayIsCorrectlyPopulated() {
        List<String> sent = manager.getSentMessages().stream()
                .filter(text -> !text.isEmpty())
                .toList();

        assertEquals(List.of(MSG_1, MSG_4), sent);
    }

    @Test
    void longestStoredMessageIsDisplayed() {
        assertEquals(MSG_2, manager.getLongestStoredMessage());
    }

    @Test
    void searchByMessageIdReturnsMessage4() {
        String result = manager.searchMessageByID("1000000004");

        assertTrue(result.contains(MSG_4));
        assertTrue(result.contains("0838884567"));
    }

    @Test
    void searchByRecipientReturnsBothMessages() {
        String result = manager.searchMessagesByRecipient("+27838884567");

        assertEquals(MSG_2 + "\n" + MSG_5, result);
    }

    @Test
    void deleteMessageUsingHashRemovesMessage2() throws IOException {
        String result = manager.deleteMessageByHash(HASH_2, jsonFile);

        assertEquals(
                "Message: \"" + MSG_2 + "\" successfully deleted.",
                result
        );
        assertFalse(manager.getStoredMessages().contains(MSG_2));
    }

    @Test
    void reportShowsHashRecipientAndMessage() {
        String report = manager.getStoredMessagesReport();

        assertTrue(report.contains(HASH_2));
        assertTrue(report.contains("+27838884567"));
        assertTrue(report.contains(MSG_2));
        assertTrue(report.contains(MSG_5));
    }
}