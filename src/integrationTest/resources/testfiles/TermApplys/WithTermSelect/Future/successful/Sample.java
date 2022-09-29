package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        CompletableFuture.completedFuture("a");
    }
}