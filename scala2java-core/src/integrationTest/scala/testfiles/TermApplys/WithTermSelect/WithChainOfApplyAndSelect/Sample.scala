package testfiles.TermApplys.WithTermSelect.WithChainOfApplyAndSelect

import testfilesext.SampleClass

class Sample {
  def foo(): Unit = {
    val s = new SampleClass()
    s.func1("one").func2("two").func3("three")
  }
}