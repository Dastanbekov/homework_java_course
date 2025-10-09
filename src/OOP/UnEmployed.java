package OOP;

public class UnEmployed extends Human {
    private int yearsUnemployed;

    public UnEmployed(String name, int age, int gender, int yearsUnemployed) {
        super(name, age, gender);
        this.yearsUnemployed = yearsUnemployed;
    }

    @Override
    public void printMessage() {
        System.out.println(name + " has been unemployed for " + yearsUnemployed + " years.");
    }
}
