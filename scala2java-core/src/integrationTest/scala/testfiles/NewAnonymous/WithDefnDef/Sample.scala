package testfiles.NewAnonymous.WithDefnDef

import testfilesext.SampleClass

class Sample {

  val foo: SampleClass = new SampleClass {
    def bar(x: Int): Int = x + 1
  }
}
