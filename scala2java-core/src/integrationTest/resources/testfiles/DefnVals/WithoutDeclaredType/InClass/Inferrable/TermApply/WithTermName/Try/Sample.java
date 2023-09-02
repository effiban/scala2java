package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import io.vavr.control.Try;

public class Sample {
    public final Try<String> x = Try.ofSupplier(() -> "abc");
}