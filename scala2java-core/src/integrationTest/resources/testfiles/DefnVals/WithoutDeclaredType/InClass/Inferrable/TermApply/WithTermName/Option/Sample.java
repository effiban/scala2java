package dummy;

import java.util.Optional;
import java.util.Optional.ofNullable;

public class Sample {
    public final Optional<String> x = ofNullable("abc");
}