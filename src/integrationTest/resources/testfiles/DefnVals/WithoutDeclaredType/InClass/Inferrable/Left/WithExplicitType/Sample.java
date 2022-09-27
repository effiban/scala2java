package dummy;


public class Sample {
    public final Either<Exception, String> x = Left.<Exception, String>(new RuntimeException);

    public Sample() {
    }
}