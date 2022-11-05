package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public abstract sealed class Sample permits Sample1, Sample2 {
    private final int x;

    public Sample(final int x) {
        this.x = x;
    }
}

public non-sealed class Sample1 extends Sample {

    public Sample1() {
        super(1);
    }
}

public non-sealed class Sample2 extends Sample {

    public Sample2() {
        super(2);
    }
}