package dummy;


public class Sample {
    public final Either<Err, String> x = Right.<Err, String>("abc");

    public Sample() {
    }
}