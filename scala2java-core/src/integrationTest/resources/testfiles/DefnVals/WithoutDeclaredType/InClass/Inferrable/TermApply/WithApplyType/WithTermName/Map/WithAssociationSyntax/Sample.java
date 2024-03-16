package dummy;

import java.util.Map;
import java.util.Map.entry;

public class Sample {
    public final Map<String, int> x = Map.<String, int>ofEntries(entry("a", 1), entry("b", 2));
}