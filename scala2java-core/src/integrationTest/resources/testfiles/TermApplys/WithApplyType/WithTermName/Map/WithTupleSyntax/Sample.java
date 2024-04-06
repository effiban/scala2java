package dummy;

import java.util.Map;

public class Sample {

    public void foo() {
        Map.<String, int>ofEntries(Map.entry("a", 1), Map.entry("b", 2));
    }
}