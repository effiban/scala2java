package dummy;


public class Sample {

    public String foo() {
        try {
            return "ok";
        }
        catch (IllegalStateException e) {
            return "illegal state";
        }
        catch (IllegalArgumentException e) {
            return "illegal argument";
        }
    }
}