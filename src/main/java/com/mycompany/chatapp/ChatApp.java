package com.mycompany.chatapp;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author RC_Student_lab
 */
public class ChatApp { // Ensure the class name matches the file name (ChatApp.java)

    static JSONArray messageStorage = new JSONArray(); // JSON array to store messages
    static int totalMessages = 0; // Counter for total messages sent

    public static void main(String[] args) {
        // Display a welcome message with simple instructions
        JOptionPane.showMessageDialog(null, """
                                            Welcome to QuickChat!
                                            
                                            To use this app:
                                            1. Log in with your user name and password.
                                            2. Send messages or quit.
                                            3. Messages are saved securely when you exit.
                                            
                                            Let's get started!""");

        // Login first
        String user = JOptionPane.showInputDialog("Enter your user name:");
        String pass = JOptionPane.showInputDialog("Enter your password:");

        // Validate credentials
        if (!"Thabiso".equals(user)) {
            JOptionPane.showMessageDialog(null, "Login failed! Incorrect user name.");
            return;
        }

        if (!"2005".equals(pass)) {
            JOptionPane.showMessageDialog(null, "Login failed! Incorrect password.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Login successful! Welcome, Thabiso.");

        // Menu loop
        String[] options = {"Send Messages", "Show Recently Sent Messages", "Quit"};

        while (true) {
            int choice = JOptionPane.showOptionDialog(
                null,
                "What would you like to do?",
                "QuickChat Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );

            switch (choice) {
                case 0 -> sendMultipleMessages();
                case 1 -> showMessages();
                case 2 -> {
                    saveMessagesToJSON();
                    JOptionPane.showMessageDialog(null, "Thank you for using QuickChat! Goodbye!");
                    return;
                }
                default -> JOptionPane.showMessageDialog(null, "Invalid option. Please try again.");
            }
        }
    }

    /**
     * Sends multiple messages based on user input.
     */
    static void sendMultipleMessages() {
        try {
            // Ask how many messages to send
            String numMessagesInput = JOptionPane.showInputDialog("How many messages would you like to send? (e.g., 3)");
            int numMessages = Integer.parseInt(numMessagesInput);

            if (numMessages <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number of messages.");
                return;
            }

            // Loop to send multiple messages
            for (int i = 0; i < numMessages; i++) {
                String recipient = JOptionPane.showInputDialog("Enter recipient username: (e.g., Alice)");
                String message = JOptionPane.showInputDialog("Enter your message: (e.g., Hello!)");

                // Validate recipient
                if (!validateRecipient(recipient)) {
                    JOptionPane.showMessageDialog(null, "Invalid recipient username. Must be 10 characters or less.");
                    continue;
                }

                // Validate message length
                if (message.length() > 250) {
                    JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.");
                    continue;
                }

                // Generate Unique Message ID
                String messageId = generateUniqueMessageID();

                // Generate Message Hash
                String messageHash = generateMessageHash(messageId, totalMessages + 1, message);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("MessageID", messageId);
                jsonMessage.put("Recipient", recipient);
                jsonMessage.put("Message", message);
                jsonMessage.put("MessageHash", messageHash);

                messageStorage.add(jsonMessage);

                JOptionPane.showMessageDialog(null,
                    """
                    Message Sent!
                    
                    Message ID: """ + messageId + "\n" +
                    "Recipient: " + recipient + "\n" +
                    "Message: " + message + "\n" +
                    "Message Hash: " + messageHash);

                totalMessages++;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
        }
    }

    /**
     * Displays a "Coming Soon" message for the "Show Recently Sent Messages" feature.
     */
    static void showMessages() {
        JOptionPane.showMessageDialog(null, "This feature is coming soon!");
    }

    /**
     * Validates the recipient's username.
     * @param recipient The recipient's username.
     * @return true if valid, false otherwise.
     */
    static boolean validateRecipient(String recipient) {
        // Check if the recipient is null or longer than 10 characters
        // Invalid if null or too long
        // Valid if within length limit
        
        return !(recipient == null || recipient.length() > 10); 
    }

    /**
     * Generates a unique 10-digit Message ID.
     * @return A randomly generated 10-digit number as a string.
     */
    static String generateUniqueMessageID() {
        Random random = new Random();
        long id = Math.abs(random.nextLong()) % 1_000_000_000L; // Generate a 9-digit number
        return String.format("%010d", id); // Pad with leading zeros to make it 10 digits
    }

    /**
     * Generates a Message Hash based on the given parameters.
     * @param messageId The unique message ID.
     * @param numMessagesSent The total number of messages sent.
     * @param message The message content.
     * @return The generated Message Hash.
     */
    static String generateMessageHash(String messageId, int numMessagesSent, String message) {
        // Extract the first two digits of the Message ID
        String firstTwoDigits = messageId.substring(0, 2);

        // Get the total number of messages sent
        String numMessagesSentStr = String.valueOf(numMessagesSent);

        // Extract the first and last words of the message
        String[] words = message.split("\\s+");
        String firstWord = words[0].toUpperCase();
        String lastWord = words[words.length - 1].toUpperCase();

        // Combine all parts to form the Message Hash
        return firstTwoDigits + ":" + numMessagesSentStr + ":" + firstWord + lastWord;
    }

    /**
     * Saves the messages to a JSON file.
     */
    static void saveMessagesToJSON() {
        try (FileWriter file = new FileWriter("storedMessages.json")) {
            file.write(messageStorage.toJSONString());
            file.flush();
            System.out.println("Messages saved to storedMessages.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}