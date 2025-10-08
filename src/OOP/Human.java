package OOP;

public class Human{
    String name;
    int age;
    int gender;

    public Human(String name, int age, int gender){
        this.name = name;
        this.age = age;
        this.gender=gender;
    }

    public void printMessage(){
        System.out.println(this.name+" "+this.age+" "+this.gender);
    }

}