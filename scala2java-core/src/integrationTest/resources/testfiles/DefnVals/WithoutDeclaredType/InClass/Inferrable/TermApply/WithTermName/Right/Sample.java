package dummy;

import io.vavr.control.Either;
import io.vavr.control.Either.right;

public class Sample {
    public final Either<String> x = right("abc");
}