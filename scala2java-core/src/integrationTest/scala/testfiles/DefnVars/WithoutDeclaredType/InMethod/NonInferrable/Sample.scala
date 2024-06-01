package testfiles.DefnVars.WithoutDeclaredType.InMethod.NonInferrable

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    var x = SampleObject
  }
}