package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface Sample {

    void foo(@MyAnnot(name = "myName") final String param1, final int param2);
}