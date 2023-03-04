package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {

    public int foo(final String str) {
        return switch (str) {
            case "one" -> 1;
            case "two" -> 2;
            default -> 0;
        }
        ;
    }
}