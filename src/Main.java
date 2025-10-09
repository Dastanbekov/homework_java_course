import OOP.Human;
import OOP.Employed;
import OOP.UnEmployed;

public class Main {
    public static void main(String[] args) {
        Human someone = new Human("John", 25, 1);
        Employed worker = new Employed("Alice", 30, 2, "Google");
        UnEmployed person = new UnEmployed("Bob", 40, 1, 3);

        someone.printMessage();
        worker.printMessage();
        person.printMessage();
    }
}
