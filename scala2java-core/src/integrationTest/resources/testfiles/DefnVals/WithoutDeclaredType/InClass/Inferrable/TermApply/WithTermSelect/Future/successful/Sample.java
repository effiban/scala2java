package dummy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture.completedFuture;

public class Sample {
    public final CompletableFuture<String> x = completedFuture("abc");
}