package dummy;


public class Sample {

    public void foo() {
        Try.<int>ofSupplier(() -> 1);
    }
}