package dummy;

import java.io.*;
import java.lang.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Sample {
    public final Either<Exception, String> x = Either.<Exception, String>left(new RuntimeException());

    public Sample() {
    }
}