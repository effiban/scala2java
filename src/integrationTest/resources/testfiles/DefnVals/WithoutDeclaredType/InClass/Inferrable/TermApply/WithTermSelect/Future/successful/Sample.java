package dummy;


public class Sample {
    public final CompletableFuture<String> x = CompletableFuture.completedFuture("abc");

    public Sample() {
    }
}