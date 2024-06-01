package testfiles.Trys.TryCatchCases.Basic.OneCase

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    try {
      SampleObject.func1()
    } catch {
      case e: Throwable => SampleObject.func13(e)
    }
  }
}
