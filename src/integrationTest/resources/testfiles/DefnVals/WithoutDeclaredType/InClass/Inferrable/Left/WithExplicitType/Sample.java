package dummy;


public class Sample {
    public final Either<Exception, String> x = Either.<Exception, String>left(new RuntimeException());

    public Sample() {
    }
}