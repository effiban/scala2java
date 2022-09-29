package dummy;


public class Sample {
    public final CompletableFuture<String> x = CompletableFuture.supplyAsync("abc");

    public Sample() {
    }
}