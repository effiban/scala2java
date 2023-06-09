package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {

    public void foo() {
        try {
            doSomething();
        }
        catch (IllegalStateException e) {
            handleIllegalState(e);
        }
        catch (Throwable __) {
            handleError();
        }
    }
}