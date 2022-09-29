package dummy;


public class Sample {
    public final CompletableFuture<RuntimeException> x = CompletableFuture.failedFuture(new RuntimeException());

    public Sample() {
    }
}