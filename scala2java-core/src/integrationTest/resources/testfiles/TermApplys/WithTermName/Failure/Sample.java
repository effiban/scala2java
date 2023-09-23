package dummy;


public class Sample {

    public void foo() {
        Try.failure(new RuntimeException());
    }
}