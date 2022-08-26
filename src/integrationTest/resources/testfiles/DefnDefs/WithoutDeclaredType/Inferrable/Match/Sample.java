package dummy;


public class Sample {

    public Sample() {
    }

    public int foo(final String str) {
        return switch (str) {
            case "one" -> 1;
            case "two" -> 2;
            default -> 0;
        }
        ;
    }
}