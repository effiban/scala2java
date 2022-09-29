package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        Map.<String, int>ofEntries(Map.entry("a", 1), Map.entry("b", 2));
    }
}