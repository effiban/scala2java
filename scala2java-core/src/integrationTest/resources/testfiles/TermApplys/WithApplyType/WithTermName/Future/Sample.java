package dummy;

import java.util.concurrent.CompletableFuture;

public class Sample {

    public void foo() {
        CompletableFuture.<int>supplyAsync(() -> 1);
    }
}