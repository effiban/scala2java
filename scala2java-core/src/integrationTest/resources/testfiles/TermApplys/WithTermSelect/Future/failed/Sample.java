package dummy;


public class Sample {

    public void foo() {
        CompletableFuture.failedFuture(new RuntimeException());
    }
}