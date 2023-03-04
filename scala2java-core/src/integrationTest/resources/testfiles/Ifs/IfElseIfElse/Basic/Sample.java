package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {

    public void foo() {
        if (x < 3) {
            doSomething(x);
        }
        else {
            if (x < 10) {
                doSomethingElse(x);
            }
            else {
                doDefault();
            }
        }
    }
}