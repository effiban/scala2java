package dummy;


public sealed interface Sample1 permits Sample1A, Sample1B, Sample2 {
}

public non-sealed class Sample1A implements Sample1 {

    public Sample1A() {
    }
}

public non-sealed class Sample1B implements Sample1 {

    public Sample1B() {
    }
}

public sealed interface Sample2 extends Sample1 permits Sample2A, Sample2B {
}

public non-sealed class Sample2A implements Sample2 {

    public Sample2A() {
    }
}

public non-sealed class Sample2B implements Sample2 {

    public Sample2B() {
    }
}