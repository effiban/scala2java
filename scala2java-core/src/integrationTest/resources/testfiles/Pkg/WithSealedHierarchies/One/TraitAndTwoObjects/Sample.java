package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public sealed interface Sample permits Sample1, Sample2 {
}

public final class Sample1 implements Sample {
}

public final class Sample2 implements Sample {
}