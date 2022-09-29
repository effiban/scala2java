package dummy;


public class Sample {
    public final Future<String> x = CompletableFuture.<String>supplyAsync("abc");

    public Sample() {
    }
}