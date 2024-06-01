package testfiles.Trys.TryCatchCaseFinally.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    try {
      SampleObject.func3(3)
    } catch {
      case e: Throwable => SampleObject.func1()
    } finally {
      SampleObject.func2()
    }
  }
}
