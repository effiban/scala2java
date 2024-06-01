package testfiles.New.WithNonDefaultCtor.WithNamedArgs

import testfilesext.SampleClass

class Sample {
  def foo(): Unit = new SampleClass(name = "a", size = 1)
}