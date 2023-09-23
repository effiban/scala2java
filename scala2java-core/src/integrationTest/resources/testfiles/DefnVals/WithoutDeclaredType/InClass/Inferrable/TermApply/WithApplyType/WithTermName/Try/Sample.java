package dummy;

import io.vavr.control.Try;

public class Sample {
    public final Try<String> x = Try.<String>ofSupplier(() -> "abc");
}