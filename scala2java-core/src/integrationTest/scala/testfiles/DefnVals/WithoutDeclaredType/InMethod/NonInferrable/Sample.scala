package testfiles.DefnVals.WithoutDeclaredType.InMethod.NonInferrable

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    val x = SampleObject
  }
}