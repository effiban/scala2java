package dummy;

import io.vavr.control.Either;
import io.vavr.control.Either.left;

public class Sample {
    public final Either<RuntimeException> x = left(new RuntimeException());
}