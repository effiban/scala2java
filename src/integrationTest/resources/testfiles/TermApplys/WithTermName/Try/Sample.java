package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        Try.ofSupplier(() -> 1);
    }
}