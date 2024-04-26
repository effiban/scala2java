package dummy;

import io.vavr.control.Try;
import io.vavr.control.Try.failure;

public class Sample {
    public final Try<RuntimeException> x = failure(new RuntimeException());
}