package dummy;


public sealed interface Sample permits Sample1, Sample2 {
}

public non-sealed class Sample1 implements Sample {

    public Sample1() {
    }
}

public non-sealed class Sample2 implements Sample {

    public Sample2() {
    }
}