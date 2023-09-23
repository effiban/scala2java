package dummy;


public class Sample {

    public void foo() {
        Try.<int>failure(new RuntimeException());
    }
}