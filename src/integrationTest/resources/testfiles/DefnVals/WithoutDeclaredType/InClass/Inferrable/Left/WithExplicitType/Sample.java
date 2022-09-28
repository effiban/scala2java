package dummy;


public class Sample {
    public final Either<Exception, String> x = /* List(Exception, String) */Left(new RuntimeException());

    public Sample() {
    }
}