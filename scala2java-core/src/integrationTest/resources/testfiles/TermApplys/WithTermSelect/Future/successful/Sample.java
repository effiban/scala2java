package dummy;

import java.util.concurrent.CompletableFuture.completedFuture;

public class Sample {

    public void foo() {
        completedFuture("a");
    }
}