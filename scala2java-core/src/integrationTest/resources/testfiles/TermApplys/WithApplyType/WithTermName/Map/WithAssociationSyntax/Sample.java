package dummy;

import java.util.Map.entry;

public class Sample {

    public void foo() {
        Map.<String, int>ofEntries(entry("a", 1), entry("b", 2));
    }
}