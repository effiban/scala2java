package dummy;

import java.util.Map.entry;

public class Sample {

    public void foo() {
        Map.ofEntries(entry("a", 1), entry("b", 2));
    }
}