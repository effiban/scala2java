package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        Either.<Err, int>left("error");
    }
}