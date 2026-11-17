package com.mycompany.main;

// Imports Scanner so we can get user input
import java.util.Scanner;

// Part 2 imports
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

// Part 3 imports
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// Part 2: simple Message class to hold the details of one message
class Message {
    int messageNumber;
    String messageId;
    String recipient;
    String text;
    String hash;
}

// Create the Main class
public class Main {

    // Create Scanner for user input
    static Scanner input = new Scanner(System.in);

    // Store the user's entered details
    static String username;
    static String password;
    static String cellphone;

    // Stores the registered username
    static String registeredUsername;

    // Store the registered password
    static String registeredPassword;

    // Store the registered cellphone number
    static String registeredCellphone;

    // Part 2: ArrayList that stores all the messages
    static ArrayList<Message> messages = new ArrayList<>();

    // Part 2: Random object used to create the message ID
    static Random random = new Random();

    // Create the username checking method
    public static boolean checkUserName(String username) {
        // Username must have exactly 5 characters
        // and must contain an underscore
        return username.length() == 5 && username.contains("_");
    }

    // Create the password checking method
    public static boolean checkPasswordComplexity(String password) {
        /*
         * Password must:
         * - Have at least 8 characters
         * - Contain an uppercase letter
         * - Contain a lowercase letter
         * - Contain a number
         * - Contain a special character
         */
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Create the cellphone checking method
    public static boolean checkCellphoneNumber(String number) {
        // Number must start with +27 followed by exactly 9 digits
        return number.matches("^\\+27[0-9]{9}$");
    }

    // Create the registration method
    public static void registerUser() {
        boolean registrationSuccessful = false;

        while (!registrationSuccessful) {

            // Ask for username
            System.out.print("Enter your username: ");
            username = input.nextLine();

            // Ask for password
            System.out.print("Enter your password: ");
            password = input.nextLine();

            // Ask for cellphone number
            System.out.print("Enter your cellphone number (+27): ");
            cellphone = input.nextLine();

            // Check details
            boolean usernameCorrect = checkUserName(username);
            boolean passwordCorrect = checkPasswordComplexity(password);
            boolean phoneCorrect = checkCellphoneNumber(cellphone);

            // Check if all registration details are correct
            if (usernameCorrect && passwordCorrect && phoneCorrect) {

                registeredUsername = username;
                registeredPassword = password;
                registeredCellphone = cellphone;

                System.out.println("User registered successfully.");

                registrationSuccessful = true;

            } else {

                System.out.println(
                    "Registration failed. Please check the following:"
                );

                if (!usernameCorrect) {
                    System.out.println(
                        "- Username must be exactly 5 characters "
                        + "and contain an underscore."
                    );
                }

                if (!passwordCorrect) {
                    System.out.println(
                        "- Password must be at least 8 characters and contain "
                        + "an uppercase letter, lowercase letter, number, "
                        + "and special character."
                    );
                }

                if (!phoneCorrect) {
                    System.out.println(
                        "- Cellphone number must start with +27 "
                        + "followed by exactly 9 digits."
                    );
                }

                System.out.println();
            }
        }
    }

    // Create the login method
    public static boolean loginUser(
            String loginUsername,
            String loginPassword) {

        // Compare entered details with registered details
        return loginUsername.equals(registeredUsername)
                && loginPassword.equals(registeredPassword);
    }

    // Create the login status method
    public static String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {
            return "Welcome, it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // ===================== PART 2 STARTS HERE =====================

    // Creates a 10 digit message ID by adding one random digit at a time
    public static String createMessageId() {
        String id = "";

        // Loop 10 times to build a 10 digit ID
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }

        return id;
    }

    // Creates the message hash
    // Format: first two digits of ID : message number : FIRSTWORDLASTWORD
    public static String createMessageHash(
            String messageId,
            int messageNumber,
            String text) {

        // Get the first two digits of the message ID
        String firstTwoDigits = messageId.substring(0, 2);

        // Split the message into words
        String[] words = text.split(" ");

        // Get the first and last word
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        // Join everything together in capital letters
        return (firstTwoDigits + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // Asks the user for the messages and stores them in the ArrayList
    public static void sendMessages() {

        // Ask how many messages the user wants to enter
        System.out.print("How many messages do you want to enter? ");
        int numberOfMessages;

        try {
            numberOfMessages = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        // For loop runs once for every message the user wants to enter
        for (int i = 1; i <= numberOfMessages; i++) {

            System.out.println("\n--- Message " + i + " of "
                    + numberOfMessages + " ---");

            // Ask for the recipient until a correct number is entered
            String recipient = "";
            boolean recipientCorrect = false;

            while (!recipientCorrect) {
                System.out.print("Enter recipient cellphone number (+27): ");
                recipient = input.nextLine();

                if (checkCellphoneNumber(recipient)) {
                    recipientCorrect = true;
                } else {
                    System.out.println(
                        "Number must start with +27 followed by 9 digits."
                    );
                }
            }

            // Ask for the message until it is not empty and 250 or less
            String text = "";
            boolean textCorrect = false;

            while (!textCorrect) {
                System.out.print("Enter your message (max 250 characters): ");
                text = input.nextLine().trim();

                if (text.length() == 0) {
                    System.out.println("Message cannot be empty.");
                } else if (text.length() > 250) {
                    System.out.println("Message is too long.");
                } else {
                    textCorrect = true;
                }
            }

            // Create a new message and fill in its details
            Message message = new Message();

            // Message number goes up by 1 each time the loop runs
            message.messageNumber = messages.size() + 1;
            message.messageId = createMessageId();
            message.recipient = recipient;
            message.text = text;
            message.hash = createMessageHash(
                    message.messageId,
                    message.messageNumber,
                    message.text
            );

            // Add the message to the ArrayList
            messages.add(message);

            // Show the details of the message that was just added
            System.out.println("Message added successfully.");
            displayMessage(message);
        }

        // Save all the messages to a JSON file
        saveMessagesToJson();
    }

    // Displays the details of one message
    public static void displayMessage(Message message) {
        System.out.println("Message Number: " + message.messageNumber);
        System.out.println("Message ID:     " + message.messageId);
        System.out.println("Message Hash:   " + message.hash);
        System.out.println("Recipient:      " + message.recipient);
        System.out.println("Message:        " + message.text);
        System.out.println();
    }

    // Displays all the stored messages using a for-each loop
    public static void showAllMessages() {

        if (messages.isEmpty()) {
            System.out.println("No messages have been entered yet.");
            return;
        }

        System.out.println("\n--- All Messages ---");

        for (Message message : messages) {
            displayMessage(message);
        }
    }

    // Saves all the messages to a JSON file using a for-each loop
    public static void saveMessagesToJson() {

        String json = "[\n";
        int count = 0;

        for (Message message : messages) {
            count++;

            json = json + "  {\n";
            json = json + "    \"messageNumber\": " + message.messageNumber + ",\n";
            json = json + "    \"messageId\": \"" + message.messageId + "\",\n";
            json = json + "    \"messageHash\": \"" + message.hash + "\",\n";
            json = json + "    \"recipient\": \"" + message.recipient + "\",\n";
            json = json + "    \"message\": \""
                    + message.text.replace("\"", "\\\"") + "\"\n";
            json = json + "  }";

            // Add a comma after every message except the last one
            if (count < messages.size()) {
                json = json + ",";
            }

            json = json + "\n";
        }

        json = json + "]";

        // Write the text to messages.json
        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(json);
            System.out.println("Messages saved to messages.json");
        } catch (IOException e) {
            System.out.println("Could not save the messages file.");
        }
    }

    // Shows the welcome message and the menu until the user quits
    public static void showMenu() {

        // Welcome message
        System.out.println("\nWelcome to QuickChat.");

        boolean quit = false;

        // While loop keeps running until the user chooses 3 (Quit)
        while (!quit) {

            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send messages");
            System.out.println("2) Show all messages");
            System.out.println("3) Display longest message");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Read messages from JSON file");
            System.out.println("7) Display message report");
            System.out.println("8) Quit");
            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                // Stops the program crashing if letters are entered
                System.out.println("Invalid input. Please enter a number from 1 to 8.");
                continue;
            }

            if (choice == 1) {
                sendMessages();
            } else if (choice == 2) {
                showAllMessages();
            } else if (choice == 3) {
                displayLongestMessage();
            } else if (choice == 4) {
                searchByRecipient();
            } else if (choice == 5) {
                deleteMessageByHash();
            } else if (choice == 6) {
                readJsonFile();
            } else if (choice == 7) {
                displayMessageReport();
            } else if (choice == 8) {
                System.out.println("Goodbye.");
                quit = true;
            } else {
                System.out.println("Invalid choice. Please enter a number from 1 to 8.");
            }
        }
    }

    // ====================== PART 2 ENDS HERE ======================

    // ===================== PART 3 STARTS HERE =====================

    // Parallel ArrayLists: the same position in each list is the same message
    static ArrayList<String> messageIds = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();
    static ArrayList<String> recipients = new ArrayList<>();
    static ArrayList<String> messageTexts = new ArrayList<>();

    // Fills the parallel ArrayLists from the stored messages
    public static void populateArrays() {

        // Clear first so nothing is added twice
        messageIds.clear();
        messageHashes.clear();
        recipients.clear();
        messageTexts.clear();

        // For-each loop copies each message detail into its own list
        for (Message message : messages) {
            messageIds.add(message.messageId);
            messageHashes.add(message.hash);
            recipients.add(message.recipient);
            messageTexts.add(message.text);
        }
    }

    // Finds and displays the longest message
    public static void displayLongestMessage() {

        populateArrays();

        if (messageTexts.isEmpty()) {
            System.out.println("No messages have been entered yet.");
            return;
        }

        String longestMessage = "";
        int longestPosition = 0;
        int position = 0;

        // For-each loop checks the length of every message
        for (String text : messageTexts) {
            if (text.length() > longestMessage.length()) {
                longestMessage = text;
                longestPosition = position;
            }
            position++;
        }

        System.out.println("\n--- Longest Message ---");
        System.out.println("Message ID: " + messageIds.get(longestPosition));
        System.out.println("Recipient:  " + recipients.get(longestPosition));
        System.out.println("Message:    " + longestMessage);
        System.out.println("Length:     " + longestMessage.length()
                + " characters");
    }

    // Searches for all the messages sent to a particular recipient
    public static void searchByRecipient() {

        populateArrays();

        System.out.print("Enter the recipient cellphone number to search: ");
        String searchNumber = input.nextLine().trim();

        boolean found = false;
        int position = 0;

        System.out.println("\n--- Messages sent to " + searchNumber + " ---");

        // For-each loop checks every recipient in the list
        for (String recipient : recipients) {
            if (recipient.equals(searchNumber)) {
                System.out.println("Message ID: " + messageIds.get(position));
                System.out.println("Message:    " + messageTexts.get(position));
                System.out.println();
                found = true;
            }
            position++;
        }

        if (!found) {
            System.out.println("No messages found for that recipient.");
        }
    }

    // Deletes a message using its message hash
    public static void deleteMessageByHash() {

        populateArrays();

        System.out.print("Enter the message hash to delete: ");
        String searchHash = input.nextLine().trim().toUpperCase();

        int deletePosition = -1;
        int position = 0;

        // For-each loop looks for the hash in the list
        for (String hash : messageHashes) {
            if (hash.equals(searchHash)) {
                deletePosition = position;
            }
            position++;
        }

        if (deletePosition == -1) {
            System.out.println("No message found with that hash.");
            return;
        }

        String deletedText = messageTexts.get(deletePosition);

        // Remove the message from the main list and the parallel lists
        messages.remove(deletePosition);
        messageIds.remove(deletePosition);
        messageHashes.remove(deletePosition);
        recipients.remove(deletePosition);
        messageTexts.remove(deletePosition);

        System.out.println("Message \"" + deletedText + "\" successfully deleted.");

        // Update the JSON file so it matches the list
        saveMessagesToJson();
    }

    // Reads messages.json into ArrayLists and displays what was read
    // Attribution: reading a file with Files.readAllLines is based on the
    // Oracle Java documentation for java.nio.file.Files (docs.oracle.com)
    public static void readJsonFile() {

        ArrayList<String> jsonIds = new ArrayList<>();
        ArrayList<String> jsonHashes = new ArrayList<>();
        ArrayList<String> jsonRecipients = new ArrayList<>();
        ArrayList<String> jsonTexts = new ArrayList<>();

        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get("messages.json"));
        } catch (IOException e) {
            System.out.println("Could not read messages.json. "
                    + "Send some messages first.");
            return;
        }

        String currentId = "";
        String currentHash = "";
        String currentRecipient = "";

        // For-each loop goes through every line of the file
        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("\"messageId\":")) {
                currentId = getJsonValue(line);
            } else if (line.startsWith("\"messageHash\":")) {
                currentHash = getJsonValue(line);
            } else if (line.startsWith("\"recipient\":")) {
                currentRecipient = getJsonValue(line);
            } else if (line.startsWith("\"message\":")) {
                // The message is the last field, so save the whole message now
                jsonIds.add(currentId);
                jsonHashes.add(currentHash);
                jsonRecipients.add(currentRecipient);
                jsonTexts.add(getJsonValue(line));
            }
        }

        if (jsonIds.isEmpty()) {
            System.out.println("The file contains no messages.");
            return;
        }

        System.out.println("\n--- Messages read from messages.json ---");

        int position = 0;

        // For-each loop displays everything that was read from the file
        for (String id : jsonIds) {
            System.out.println("Message ID:   " + id);
            System.out.println("Message Hash: " + jsonHashes.get(position));
            System.out.println("Recipient:    " + jsonRecipients.get(position));
            System.out.println("Message:      " + jsonTexts.get(position));
            System.out.println();
            position++;
        }
    }

    // Gets the value from a JSON line like:  "recipient": "+27123456789",
    public static String getJsonValue(String line) {

        // Take everything after the colon
        String value = line.substring(line.indexOf(":") + 1).trim();

        // Remove the comma at the end (if there is one)
        if (value.endsWith(",")) {
            value = value.substring(0, value.length() - 1);
        }

        // Remove the quotation marks around the value
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        // Turn \" back into "
        return value.replace("\\\"", "\"");
    }

    // Displays a report of all the sent messages
    public static void displayMessageReport() {

        populateArrays();

        if (messageIds.isEmpty()) {
            System.out.println("No messages have been entered yet.");
            return;
        }

        System.out.println("\n========== MESSAGE REPORT ==========");

        int position = 0;

        // For-each loop prints one report entry per message
        for (String hash : messageHashes) {
            System.out.println("Message Hash: " + hash);
            System.out.println("Message ID:   " + messageIds.get(position));
            System.out.println("Recipient:    " + recipients.get(position));
            System.out.println("Message:      " + messageTexts.get(position));
            System.out.println("------------------------------------");
            position++;
        }

        System.out.println("Total messages: " + messageIds.size());
    }

    // ====================== PART 3 ENDS HERE ======================

    // Main method where the program starts
    public static void main(String[] args) {

        // Call registration method
        registerUser();

        // Prompt for login to test functionality
        System.out.println("\n--- Login ---");

        System.out.print("Enter username to login: ");
        String loginUsernameInput = input.nextLine();

        System.out.print("Enter password to login: ");
        String loginPasswordInput = input.nextLine();

        // Call login method
        boolean loginSuccessful = loginUser(
                loginUsernameInput,
                loginPasswordInput
        );

        // Call login status method
        System.out.println(returnLoginStatus(loginSuccessful));

        // Part 2: only show the menu if the login was successful
        if (loginSuccessful) {
            showMenu();
        }
    }
}
