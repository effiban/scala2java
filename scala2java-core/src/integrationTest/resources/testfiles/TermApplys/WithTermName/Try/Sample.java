package dummy;

import io.vavr.control.Try.ofSupplier;

public class Sample {

    public void foo() {
        ofSupplier(() -> 1);
    }
}