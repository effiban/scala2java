package dummy;


public class Sample {
    public final CompletableFuture<String> x = CompletableFuture.<String>supplyAsync("abc");

    public Sample() {
    }
}