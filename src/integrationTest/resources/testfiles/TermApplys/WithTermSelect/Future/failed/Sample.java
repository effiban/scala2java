package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        CompletableFuture.failedFuture(new RuntimeException());
    }
}