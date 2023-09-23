package dummy;

import java.util.concurrent.CompletableFuture;

public class Sample {
    public final CompletableFuture<int> x = CompletableFuture.<int>failedFuture(new RuntimeException());
}