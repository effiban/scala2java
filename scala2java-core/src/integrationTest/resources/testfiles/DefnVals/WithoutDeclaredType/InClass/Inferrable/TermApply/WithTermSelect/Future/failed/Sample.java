package dummy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture.failedFuture;

public class Sample {
    public final CompletableFuture<Object> x = failedFuture(new RuntimeException());
}