package dummy;

import io.vavr.control.Try;
import io.vavr.control.Try.ofSupplier;

public class Sample {
    public final Try<String> x = ofSupplier(() -> "abc");
}