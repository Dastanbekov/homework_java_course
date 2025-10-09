package OOP;

public class Employed extends Human {
    private String workName;

    public Employed(String name, int age, int gender, String workName) {
        super(name, age, gender);
        this.workName = workName;
    }

    @Override
    public void printMessage() {
        System.out.println(name + " works at " + workName);
    }

    public void printCharacteristics() {
        System.out.println(name + " | Age: " + getAge() + " | Gender: " + gender);
    }
}
