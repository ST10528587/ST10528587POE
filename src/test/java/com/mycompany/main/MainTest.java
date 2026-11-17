package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Unit tests for the QuickChat application (Parts 1, 2 and 3)
public class MainTest {

    private final PrintStream originalOut = System.out;
    private final Scanner originalInput = Main.input;
    private ByteArrayOutputStream output;

    // Runs before every test: start with an empty message list
    // and capture everything the program prints
    @BeforeEach
    public void setUp() {
        Main.messages.clear();
        new File("messages.json").delete();
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    // Runs after every test: put System.out and the Scanner back
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        Main.input = originalInput;
        Main.messages.clear();
        new File("messages.json").delete();
    }

    // Helper: creates a message and adds it to the list
    private Message addTestMessage(
            int number, String id, String recipient, String text) {

        Message message = new Message();
        message.messageNumber = number;
        message.messageId = id;
        message.recipient = recipient;
        message.text = text;
        message.hash = Main.createMessageHash(id, number, text);
        Main.messages.add(message);
        return message;
    }

    // Helper: adds three messages used by most Part 3 tests
    private void addThreeMessages() {
        addTestMessage(1, "0012345678", "+27838968976",
                "Hi Mike, can you join us for dinner tonight");
        addTestMessage(2, "1122334455", "+27838884567",
                "Ok");
        addTestMessage(3, "9988776655", "+27838968976",
                "Where are you? You are late! I have asked you to be on time.");
    }

    // ================= PART 1 TESTS =================

    @Test
    public void testUsernameValid() {
        assertTrue(Main.checkUserName("kyl_1"));
    }

    @Test
    public void testUsernameInvalid() {
        assertFalse(Main.checkUserName("kyle!!!!!"));
        assertFalse(Main.checkUserName("kyle1"));
    }

    @Test
    public void testPasswordValid() {
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordInvalid() {
        assertFalse(Main.checkPasswordComplexity("password"));
        assertFalse(Main.checkPasswordComplexity("Pass1!"));
    }

    @Test
    public void testCellphoneValid() {
        assertTrue(Main.checkCellphoneNumber("+27838968976"));
    }

    @Test
    public void testCellphoneInvalid() {
        assertFalse(Main.checkCellphoneNumber("08966553"));
        assertFalse(Main.checkCellphoneNumber("+2783896897"));
    }

    @Test
    public void testLoginSuccessful() {
        Main.registeredUsername = "kyl_1";
        Main.registeredPassword = "Ch&&sec@ke99!";

        assertTrue(Main.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertEquals("Welcome, it is great to see you again.",
                Main.returnLoginStatus(true));
    }

    @Test
    public void testLoginFailed() {
        Main.registeredUsername = "kyl_1";
        Main.registeredPassword = "Ch&&sec@ke99!";

        assertFalse(Main.loginUser("kyl_1", "wrongPassword"));
        assertEquals("Username or password incorrect, please try again.",
                Main.returnLoginStatus(false));
    }

    // ================= PART 2 TESTS =================

    @Test
    public void testMessageIdHasTenDigits() {
        String id = Main.createMessageId();

        assertEquals(10, id.length());
        assertTrue(id.matches("[0-9]{10}"));
    }

    @Test
    public void testMessageHash() {
        String hash = Main.createMessageHash(
                "0012345678", 1, "Hi Mike, can you join us for dinner tonight");

        assertEquals("00:1:HITONIGHT", hash);
    }

    @Test
    public void testMessageHashOneWord() {
        assertEquals("11:2:OKOK", Main.createMessageHash("1122334455", 2, "Ok"));
    }

    @Test
    public void testSaveMessagesCreatesFile() {
        addThreeMessages();
        Main.saveMessagesToJson();

        assertTrue(new File("messages.json").exists());
    }

    // ================= PART 3 TESTS =================

    // Arrays correctly populated
    @Test
    public void testPopulateArrays() {
        addThreeMessages();
        Main.populateArrays();

        assertEquals(3, Main.messageIds.size());
        assertEquals(3, Main.messageHashes.size());
        assertEquals(3, Main.recipients.size());
        assertEquals(3, Main.messageTexts.size());

        assertEquals("0012345678", Main.messageIds.get(0));
        assertEquals("00:1:HITONIGHT", Main.messageHashes.get(0));
        assertEquals("+27838884567", Main.recipients.get(1));
        assertEquals("Ok", Main.messageTexts.get(1));
    }

    @Test
    public void testPopulateArraysTwiceDoesNotDuplicate() {
        addThreeMessages();
        Main.populateArrays();
        Main.populateArrays();

        assertEquals(3, Main.messageTexts.size());
    }

    // Display details for the longest message
    @Test
    public void testLongestMessage() {
        addThreeMessages();
        Main.displayLongestMessage();

        String printed = output.toString();
        assertTrue(printed.contains(
                "Where are you? You are late! I have asked you to be on time."));
        assertTrue(printed.contains("9988776655"));
    }

    @Test
    public void testLongestMessageWhenEmpty() {
        Main.displayLongestMessage();

        assertTrue(output.toString().contains("No messages have been entered yet."));
    }

    // Search array for messages sent to a particular recipient
    @Test
    public void testSearchByRecipientFound() {
        addThreeMessages();
        Main.input = new Scanner("+27838968976\n");
        Main.searchByRecipient();

        String printed = output.toString();
        assertTrue(printed.contains("Hi Mike, can you join us for dinner tonight"));
        assertTrue(printed.contains("Where are you? You are late!"));
        assertFalse(printed.contains("Message:    Ok"));
    }

    @Test
    public void testSearchByRecipientNotFound() {
        addThreeMessages();
        Main.input = new Scanner("+27111111111\n");
        Main.searchByRecipient();

        assertTrue(output.toString().contains("No messages found for that recipient."));
    }

    // Delete message using message hash
    @Test
    public void testDeleteMessageByHash() {
        addThreeMessages();
        Main.input = new Scanner("00:1:HITONIGHT\n");
        Main.deleteMessageByHash();

        assertEquals(2, Main.messages.size());
        assertTrue(output.toString().contains("successfully deleted"));

        // The deleted hash must no longer be in the parallel array
        Main.populateArrays();
        assertFalse(Main.messageHashes.contains("00:1:HITONIGHT"));
    }

    @Test
    public void testDeleteMessageByHashNotFound() {
        addThreeMessages();
        Main.input = new Scanner("99:9:NOTHERE\n");
        Main.deleteMessageByHash();

        assertEquals(3, Main.messages.size());
        assertTrue(output.toString().contains("No message found with that hash."));
    }

    // Read JSON file into an array and display it
    @Test
    public void testReadJsonFile() {
        addThreeMessages();
        Main.saveMessagesToJson();
        output.reset();

        Main.readJsonFile();

        String printed = output.toString();
        assertTrue(printed.contains("0012345678"));
        assertTrue(printed.contains("00:1:HITONIGHT"));
        assertTrue(printed.contains("+27838884567"));
        assertTrue(printed.contains("Hi Mike, can you join us for dinner tonight"));
    }

    @Test
    public void testReadJsonFileMissing() {
        new File("messages.json").delete();
        Main.readJsonFile();

        assertTrue(output.toString().contains("Could not read messages.json"));
    }

    @Test
    public void testGetJsonValue() {
        assertEquals("+27838968976",
                Main.getJsonValue("\"recipient\": \"+27838968976\","));
        assertEquals("He said \"hi\"",
                Main.getJsonValue("\"message\": \"He said \\\"hi\\\"\""));
    }

    // Display message report
    @Test
    public void testMessageReport() {
        addThreeMessages();
        Main.displayMessageReport();

        String printed = output.toString();
        assertTrue(printed.contains("MESSAGE REPORT"));
        assertTrue(printed.contains("00:1:HITONIGHT"));
        assertTrue(printed.contains("1122334455"));
        assertTrue(printed.contains("+27838884567"));
        assertTrue(printed.contains("Total messages: 3"));
    }

    @Test
    public void testMessageReportWhenEmpty() {
        Main.displayMessageReport();

        assertTrue(output.toString().contains("No messages have been entered yet."));
    }
}
