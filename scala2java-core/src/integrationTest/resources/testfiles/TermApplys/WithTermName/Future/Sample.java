package dummy;

import java.util.concurrent.CompletableFuture.supplyAsync;

public class Sample {

    public void foo() {
        supplyAsync(() -> 1);
    }
}