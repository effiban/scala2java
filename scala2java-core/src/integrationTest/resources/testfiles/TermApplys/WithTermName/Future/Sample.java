package dummy;


public class Sample {

    public void foo() {
        CompletableFuture.supplyAsync(() -> 1);
    }
}