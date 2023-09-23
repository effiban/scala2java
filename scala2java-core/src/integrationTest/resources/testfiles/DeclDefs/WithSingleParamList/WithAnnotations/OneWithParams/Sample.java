package dummy;


public interface Sample {

    void foo(@MyAnnot(name = "myName") final String param1, final int param2);
}