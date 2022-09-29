package dummy;


public class Sample {
    public final Either<Err, String> x = Either.<Err, String>right("abc");

    public Sample() {
    }
}