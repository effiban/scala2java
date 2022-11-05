package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface Sample {

    @MyAnnot(name = "myName", size = 10)
    void foo();
}