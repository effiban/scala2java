package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        CompletableFuture.supplyAsync(1);
    }
}