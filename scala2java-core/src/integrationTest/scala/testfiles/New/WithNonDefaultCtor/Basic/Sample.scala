package testfiles.New.WithNonDefaultCtor.Basic

import testfilesext.SampleClass

class Sample {
  def foo(): Unit = new SampleClass("a", 1)
}