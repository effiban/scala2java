package dummy;


public class Sample {

    public Sample() {
    }

    public String foo() {
        return try {
            "ok";
        }
        catch (final IllegalStateException e) {
            "illegal state";
        }
        catch (final IllegalArgumentException e) {
            "illegal argument";
        }
        ;
    }
}