package dummy;


public class Sample {

    public Sample() {
    }

    public String foo() {
        try {
            return "ok";
        }
        catch (final IllegalStateException e) {
            return "illegal state";
        }
        catch (final IllegalArgumentException e) {
            return "illegal argument";
        }
    }
}