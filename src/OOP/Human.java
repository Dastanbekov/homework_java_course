package OOP;

public class Human {
    protected String name;
    private int age;
    public int gender;

    public Human(String name, int age, int gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age > 0) this.age = age;
    }

    public void printMessage() {
        System.out.println("This is a human named " + name);
    }
}
