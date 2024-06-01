package testfiles.NewAnonymous.WithDefnVal

import testfilesext.SampleClass

class Sample {

  val foo: SampleClass = new SampleClass {
    val bar: Int = 3
  }
}
