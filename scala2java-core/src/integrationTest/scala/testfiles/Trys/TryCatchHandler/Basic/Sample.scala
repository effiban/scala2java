package testfiles.Trys.TryCatchHandler.Basic

import testfilesext.SampleObject

class Sample {

  def foo(): Unit = {
    try {
      SampleObject.func1()
    } catch SampleObject.partialFunc
  }
}
