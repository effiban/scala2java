package dummy;

import java.util.concurrent.CompletableFuture;

public class Sample {
    public final CompletableFuture<Object> x = CompletableFuture.failedFuture(new RuntimeException());
}