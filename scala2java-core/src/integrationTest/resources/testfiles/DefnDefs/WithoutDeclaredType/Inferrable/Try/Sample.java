package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {

    public Sample() {
    }

    public String foo() {
        try {
            return "ok";
        }
        catch (final IllegalStateException e) {
            return "illegal state";
        }
        catch (final IllegalArgumentException e) {
            return "illegal argument";
        }
    }
}