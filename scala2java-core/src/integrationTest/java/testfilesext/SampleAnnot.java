package testfilesext;

public @interface SampleAnnot {

    String name() default "";

    int size() default 0;
}
