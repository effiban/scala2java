package testfiles.Trys.TryFinally.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    try {
      SampleObject.func1()
    } finally {
      SampleObject.func2()
    }
  }
}
