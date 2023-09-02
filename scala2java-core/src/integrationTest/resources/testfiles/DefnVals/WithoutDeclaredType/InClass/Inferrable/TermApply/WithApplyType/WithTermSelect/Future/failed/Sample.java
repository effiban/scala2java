package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.CompletableFuture;

public class Sample {
    public final CompletableFuture<int> x = CompletableFuture.<int>failedFuture(new RuntimeException());
}