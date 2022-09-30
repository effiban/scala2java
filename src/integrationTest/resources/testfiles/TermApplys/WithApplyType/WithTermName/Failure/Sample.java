package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        Try.<int>failure(new RuntimeException());
    }
}