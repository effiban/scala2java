package dummy;


public class Sample {
    public final Either<RuntimeException> x = Left(new RuntimeException);

    public Sample() {
    }
}