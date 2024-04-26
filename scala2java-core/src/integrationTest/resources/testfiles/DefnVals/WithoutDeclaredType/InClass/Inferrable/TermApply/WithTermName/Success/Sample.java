package dummy;

import io.vavr.control.Try;
import io.vavr.control.Try.success;

public class Sample {
    public final Try<String> x = success("abc");
}