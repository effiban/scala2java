package testfiles.Trys.TryCatchCases.Basic.TwoCases.WithWildcard

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    try {
      SampleObject.func1()
    } catch {
      case e: IllegalStateException => SampleObject.func13(e)
      case _ : Throwable => SampleObject.func2()
    }
  }
}
