package dummy;

import java.util.concurrent.CompletableFuture.failedFuture;

public class Sample {

    public void foo() {
        failedFuture(new RuntimeException());
    }
}