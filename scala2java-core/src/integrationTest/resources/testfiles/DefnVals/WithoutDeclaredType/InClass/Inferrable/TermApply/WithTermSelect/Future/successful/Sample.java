package dummy;

import java.util.concurrent.CompletableFuture;

public class Sample {
    public final CompletableFuture<String> x = CompletableFuture.completedFuture("abc");
}