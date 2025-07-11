package testfiles.DefnVals.WithoutDeclaredType.InMethod.Inferrable

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    val x = SampleObject
  }
}