package dummy;


public class Sample {
    public final Future<String> x = CompletableFuture.supplyAsync("abc");

    public Sample() {
    }
}