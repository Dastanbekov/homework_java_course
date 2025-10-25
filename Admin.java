public class Admin extends User {
    public Admin(String username) {
        super(username);
    }

    /**
     * Stores the message in the "ADMIN <message>" format.
     */
    @Override
    public void sendMessage(String message) {
        super.sendMessage("ADMIN " + message);
    }
}
