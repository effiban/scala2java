package dummy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture.supplyAsync;

public class Sample {
    public final CompletableFuture<String> x = supplyAsync(() -> "abc");
}