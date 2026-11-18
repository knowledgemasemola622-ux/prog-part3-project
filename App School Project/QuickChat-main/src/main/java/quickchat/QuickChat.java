
package quickchat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class QuickChat {

    private static final Path JSON_FILE = Path.of("messages.json");

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        MessageManager manager = new MessageManager();

        System.out.println("Welcome to QuickChat.");

        // Load previously stored messages.
        try {
            manager.loadStoredMessages(JSON_FILE);
        } catch (IOException e) {
            System.out.println(
                    "Could not load stored messages: " + e.getMessage()
            );
        }

        // Registration.
        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        Login user = new Login(firstName, lastName);

        boolean registered = false;

        while (!registered) {

            System.out.print("Create a username: ");
            String username = input.nextLine();

            System.out.print("Create a password: ");
            String password = input.nextLine();

            System.out.print("Enter your cellphone number (+27 format): ");
            String cellNumber = input.nextLine();

            String registrationResult =
                    user.registerUser(username, password, cellNumber);

            System.out.println(registrationResult);

            registered = registrationResult.equals(
                    "User registered successfully."
            );
        }

        // Login.
        boolean loggedIn = false;

        while (!loggedIn) {

            System.out.print("\nEnter your username to log in: ");
            String username = input.nextLine();

            System.out.print("Enter your password: ");
            String password = input.nextLine();

            loggedIn = user.loginUser(username, password);

            System.out.println(user.returnLoginStatus());
        }

        // The logged-in user's cellphone number is the sender.
        String sender = user.getRegisteredCellNumber();

        // Main menu.
        while (true) {

            System.out.println("\nPlease select an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");

            System.out.print("Enter your choice: ");
            String option = input.nextLine();

            switch (option) {

                case "1":

                    System.out.print(
                            "How many messages would you like to send? "
                    );

                    int numberOfMessages;

                    try {
                        numberOfMessages = Integer.parseInt(input.nextLine());

                        if (numberOfMessages <= 0) {
                            System.out.println(
                                    "Please enter a number greater than zero."
                            );
                            break;
                        }

                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid input. Please enter a whole number."
                        );
                        break;
                    }

                    // Process each message.
                    for (int i = 1; i <= numberOfMessages; i++) {

                        int messageNumber = i - 1;

                        System.out.println(
                                "\n========== MESSAGE " + i + " =========="
                        );

                        // Validate recipient.
                        String recipient;

                        while (true) {

                            System.out.print(
                                    "Enter recipient cellphone number (+27 format): "
                            );

                            recipient = input.nextLine();

                            Message messageCheck = new Message(
                                    messageNumber,
                                    recipient,
                                    "Test"
                            );

                            String recipientResult =
                                    messageCheck.checkRecipientCell();

                            if (recipientResult.equals(
                                    "Cell phone number successfully captured."
                            )) {
                                System.out.println(recipientResult);
                                break;
                            } else {
                                System.out.println(recipientResult);
                            }
                        }

                        // Validate message length.
                        String messageText;

                        while (true) {

                            System.out.print("Enter your message: ");
                            messageText = input.nextLine();

                            Message messageCheck = new Message(
                                    messageNumber,
                                    recipient,
                                    messageText
                            );

                            String lengthResult =
                                    messageCheck.checkMessageLength();

                            System.out.println(lengthResult);

                            if (lengthResult.equals(
                                    "Message ready to send."
                            )) {
                                break;
                            }
                        }

                        // Create the actual message.
                        Message message = new Message(
                                messageNumber,
                                recipient,
                                messageText
                        );

                        System.out.println(
                                "\nMessage ID generated: "
                                        + message.getMessageID()
                        );

                        System.out.println(
                                "\nChoose what to do with your message:"
                        );

                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message");

                        System.out.print("Enter your choice: ");
                        String messageChoice = input.nextLine();

                        // Process the selected action.
                        switch (messageChoice) {

                            case "1":
                                System.out.println(message.SentMessage());
                                break;

                            case "2":
                                message.createMessageHash();
                                System.out.println(
                                        message.disregardMessage()
                                );
                                break;

                            case "3":
                                System.out.println(message.storeMessage());
                                break;

                            default:
                                System.out.println(
                                        "Invalid choice. Message was not processed."
                                );
                                message.disregardMessage();
                                break;
                        }

                        // Register the message and its sender.
                        try {
                            manager.recordMessage(
                                    message,
                                    sender,
                                    JSON_FILE
                            );
                        } catch (IOException e) {
                            System.out.println(
                                    "Error recording message: "
                                            + e.getMessage()
                            );
                        }

                        // Display message details.
                        System.out.println(message.printMessages());

                        System.out.println("\n================================");
                    }

                    // Display total successfully sent messages.
                    System.out.println(
                            "\nTotal messages successfully sent: "
                                    + new Message(
                                            0,
                                            "+27000000000",
                                            "Count"
                                    ).returnTotalMessages()
                    );

                    break;

                case "2":

                    


    System.out.println("\n========== RECENTLY SENT MESSAGES ==========");

    boolean foundMessages = false;

    for (String message : manager.getSentMessages()) {

        if (message != null && !message.isEmpty()) {

            System.out.println("Message: " + message);
            System.out.println("--------------------------------");
            foundMessages = true;
        }
    }

    if (!foundMessages) {
        System.out.println("No recently sent messages found.");
    }

    break;


                case "3":

                    System.out.println(
                            "Thank you for using QuickChat. Goodbye!"
                    );

                    input.close();
                    return;

                case "4":

                    displayStoredMessagesMenu(input, manager);

                    break;

                default:

                    System.out.println(
                            "Invalid option. Please select 1, 2, 3 or 4."
                    );
            }
        }
    }

    // Part 3: Stored Messages menu.
    private static void displayStoredMessagesMenu(
            Scanner input,
            MessageManager manager) {

        boolean returnToMainMenu = false;

        while (!returnToMainMenu) {

            System.out.println("\n========== STORED MESSAGES ==========");
            System.out.println("1) Display sender and recipient");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search message by ID");
            System.out.println("4) Search messages by recipient");
            System.out.println("5) Delete message using message hash");
            System.out.println("6) Display full stored messages report");
            System.out.println("7) Return to main menu");

            System.out.print("Enter your choice: ");
            String choice = input.nextLine();

            switch (choice) {

                case "1":

                    System.out.println("\nSender and recipient details:");

                    System.out.println(
                            manager.displayStoredSendersAndRecipients()
                    );

                    break;

                case "2":

                    System.out.println("\nLongest stored message:");

                    System.out.println(
                            manager.getLongestStoredMessage()
                    );

                    break;

                case "3":

                    System.out.print("Enter the message ID: ");
                    String messageID = input.nextLine();

                    System.out.println(
                            manager.searchMessageByID(messageID)
                    );

                    break;

                case "4":

                    System.out.print("Enter the recipient number: ");
                    String recipient = input.nextLine();

                    System.out.println(
                            manager.searchMessagesByRecipient(recipient)
                    );

                    break;

                case "5":

                    System.out.print("Enter the message hash: ");
                    String messageHash = input.nextLine();

                    try {
                        System.out.println(
                                manager.deleteMessageByHash(
                                        messageHash,
                                        JSON_FILE
                                )
                        );
                    } catch (IOException e) {
                        System.out.println(
                                "Error deleting message: " + e.getMessage()
                        );
                    }

                    break;

                case "6":

                    System.out.println("\nFULL STORED MESSAGES REPORT");

                    System.out.println(
                            manager.getStoredMessagesReport()
                    );

                    break;

                case "7":

                    returnToMainMenu = true;

                    break;

                default:

                    System.out.println(
                            "Invalid option. Please choose between 1 and 7."
                    );
            }
        }
    }
}
