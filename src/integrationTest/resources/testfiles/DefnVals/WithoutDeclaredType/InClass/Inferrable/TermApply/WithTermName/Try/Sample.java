package dummy;


public class Sample {
    public final Try<String> x = Try.ofSupplier(() -> "abc");

    public Sample() {
    }
}