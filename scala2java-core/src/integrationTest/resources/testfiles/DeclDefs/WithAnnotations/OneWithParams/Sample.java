package dummy;


public interface Sample {

    @MyAnnot(name = "myName", size = 10)
    void foo();
}