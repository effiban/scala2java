package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {
    private final String param1;
    private final int param2;

    public Sample(@MyAnnot(name = "myName", size = 10) final String param1, final int param2) {
        this.param1 = param1;
        this.param2 = param2;
    }
}