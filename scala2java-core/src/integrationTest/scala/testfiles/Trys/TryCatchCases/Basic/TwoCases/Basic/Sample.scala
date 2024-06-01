package testfiles.Trys.TryCatchCases.Basic.TwoCases.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    try {
      SampleObject.func1()
    } catch {
      case e: IllegalStateException => SampleObject.func13(e)
      case e: Throwable => SampleObject.func14(e)
    }
  }
}
