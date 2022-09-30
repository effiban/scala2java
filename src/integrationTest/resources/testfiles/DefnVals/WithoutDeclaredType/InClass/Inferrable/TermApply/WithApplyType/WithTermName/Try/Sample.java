package dummy;


public class Sample {
    public final Try<String> x = Try.<String>ofSupplier(() -> "abc");

    public Sample() {
    }
}