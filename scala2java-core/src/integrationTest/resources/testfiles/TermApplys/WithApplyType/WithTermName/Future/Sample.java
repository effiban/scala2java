package dummy;


public class Sample {

    public void foo() {
        CompletableFuture.<int>supplyAsync(() -> 1);
    }
}