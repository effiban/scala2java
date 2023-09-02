package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.CompletableFuture;

public class Sample {
    public final CompletableFuture<Object> x = CompletableFuture.failedFuture(new RuntimeException());
}