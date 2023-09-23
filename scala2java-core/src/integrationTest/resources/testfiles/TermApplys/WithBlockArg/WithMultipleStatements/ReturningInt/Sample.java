package dummy;


public class Sample {

    public void foo() {
        doSomething(((Supplier<int>)() ->  {
            doFirst();
            doSecond();
            3;
            }
            ).get());
    }
}