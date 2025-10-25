import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Part 3: ArrayList<User> with multiple users including at least one admin
        List<User> users = new ArrayList<>();
        User u1 = new User("Alice");
        User u2 = new User("Bob");
        Admin a1 = new Admin("Moderator");

        users.add(u1);
        users.add(u2);
        users.add(a1);

        // Simulate a chat: each user sends several messages
        u1.sendMessage("Hello everyone");
        u1.sendMessage("I think this chat is fun");
        u2.sendMessage("this chat is bad but fun");
        u2.sendMessage("Sometimes people say f*ck or bi*ch here :(");
        a1.sendMessage("Please follow the rules");
        a1.sendMessage("No offensive words, thanks!");

        // Afterward: Print all messages, filter them, run analysis
        for (User u : users) {
            System.out.println("==================================================");
            u.printMessages();
            System.out.println("  -- Filtered & Analysis --");
            for (String msg : u.getMessages()) {
                String filtered = u.filterMessage(msg);
                System.out.println("  Original: " + msg);
                System.out.println("  Filtered: " + filtered);
                u.analyzeMessage(msg);
            }
        }
    }
}
