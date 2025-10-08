package OOP;

public class Employed extends Human {
    String WorkName;
    public Employed(String name, int age, int gender, String WorkName) {
        super(name,age,gender);
        this.WorkName=WorkName;
    }

    public void printCharasteristics(){
        System.out.println(this.name+" "+this.age+" "+this.gender);
    }
}
