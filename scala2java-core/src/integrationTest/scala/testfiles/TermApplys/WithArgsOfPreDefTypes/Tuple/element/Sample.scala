package testfiles.TermApplys.WithArgsOfPreDefTypes.Tuple.element

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    val x = (1, 2)
    SampleObject.func3(x._1)
  }
}