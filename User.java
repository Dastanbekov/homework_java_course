import java.util.ArrayList;
import java.util.List;

public class User {
    protected String username;
    protected List<String> messages;

    public User(String username) {
        this.username = username;
        this.messages = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<String> getMessages() {
        return messages;
    }

    /**
     * Adds the original message to this user's message list.
     */
    public void sendMessage(String message) {
        messages.add(message);
    }

    /**
     * Prints all stored messages for this user.
     */
    public void printMessages() {
        System.out.println("Messages from " + username + ":");
        if (messages.isEmpty()) {
            System.out.println("  (no messages)");
        } else {
            for (String msg : messages) {
                System.out.println("  " + msg);
            }
        }
    }

    /**
     * Uses Regex to replace bad words with ** and returns the filtered message.
     * You can add more patterns to the list as needed.
     */
    public String filterMessage(String message) {
        if (message == null) return null;
        String filtered = message;

        // Very simple examples of "bad word" patterns (case-insensitive).
        // This intentionally allows some leetspeak/punctuation in between letters.
        String[] patterns = new String[]{
            "(?i)\\bf\\W*?u?\\W*c\\W*k\\b",   // f*ck variations
            "(?i)\\bb\\W*i\\W*t\\W*c\\W*h\\b" // bi*ch variations
        };

        for (String p : patterns) {
            filtered = filtered.replaceAll(p, "**");
        }
        return filtered;
    }

    /**
     * Analyzes a message by printing:
     * - length
     * - number of words
     * - whether it starts with a capital letter
     */
    public void analyzeMessage(String message) {
        if (message == null) {
            System.out.println("  (null message)");
            return;
        }
        String trimmed = message.trim();
        int length = message.length();
        int wordCount = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        boolean startsWithCapital = false;
        if (!trimmed.isEmpty()) {
            char first = trimmed.charAt(0);
            startsWithCapital = Character.isUpperCase(first);
        }

        System.out.println("  Length: " + length);
        System.out.println("  Words: " + wordCount);
        System.out.println("  Starts with capital: " + (startsWithCapital ? "Yes" : "No"));
    }
}
