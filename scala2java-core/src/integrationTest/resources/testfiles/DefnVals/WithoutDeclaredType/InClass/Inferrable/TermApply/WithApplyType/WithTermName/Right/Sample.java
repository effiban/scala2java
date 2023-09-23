package dummy;

import io.vavr.control.Either;

public class Sample {
    public final Either<Err, String> x = Either.<Err, String>right("abc");
}