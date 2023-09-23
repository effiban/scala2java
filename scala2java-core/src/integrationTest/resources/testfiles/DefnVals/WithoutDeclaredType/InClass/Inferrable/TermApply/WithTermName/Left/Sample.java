package dummy;

import io.vavr.control.Either;

public class Sample {
    public final Either<RuntimeException> x = Either.left(new RuntimeException());
}