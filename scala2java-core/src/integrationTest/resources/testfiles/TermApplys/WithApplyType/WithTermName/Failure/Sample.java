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

    public void foo() {
        Try.<int>failure(new RuntimeException());
    }
}