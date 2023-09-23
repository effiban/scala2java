package dummy;

import io.vavr.control.Either;

public class Sample {
    public final Either<Exception, String> x = Either.<Exception, String>left(new RuntimeException());
}