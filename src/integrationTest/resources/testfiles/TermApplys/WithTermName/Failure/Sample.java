package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        Try.failure(new RuntimeException());
    }
}