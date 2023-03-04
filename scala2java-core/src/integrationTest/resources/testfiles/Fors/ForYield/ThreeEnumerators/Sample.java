package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {

    public void foo() {
        xs.flatMap(x -> ys.flatMap(y -> zs.map(z -> doSomething(x,
            y,
            z))));
    }
}