package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {

    public void foo() {
        IntStream.rangeClosed(1, 4).forEach(i -> doSomething(i));
    }
}