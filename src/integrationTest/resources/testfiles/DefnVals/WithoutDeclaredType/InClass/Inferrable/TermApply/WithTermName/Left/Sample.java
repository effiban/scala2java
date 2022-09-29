package dummy;


public class Sample {
    public final Either<RuntimeException> x = Either.left(new RuntimeException());

    public Sample() {
    }
}