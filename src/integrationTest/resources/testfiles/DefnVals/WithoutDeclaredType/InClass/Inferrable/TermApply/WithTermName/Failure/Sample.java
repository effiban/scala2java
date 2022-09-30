package dummy;


public class Sample {
    public final Try<RuntimeException> x = Try.failure(new RuntimeException());

    public Sample() {
    }
}