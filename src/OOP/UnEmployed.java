package OOP;

public class UnEmployed extends Human {
    int years_unEmployed;
    public UnEmployed(String name, int age, int gender,int years_unEmployed) {
        super(name,age,gender);
        this.years_unEmployed = years_unEmployed;
    }

}
