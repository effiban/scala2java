package dummy;

import io.vavr.control.Try;

public class Sample {
    public final Try<RuntimeException> x = Try.failure(new RuntimeException());
}