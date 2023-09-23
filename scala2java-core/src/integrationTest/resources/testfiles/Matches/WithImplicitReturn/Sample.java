package dummy;


public class Sample {

    public int foo() {
        return switch (str) {
            case "one" -> 1;
            case "two" -> 2;
        }
        ;
    }
}