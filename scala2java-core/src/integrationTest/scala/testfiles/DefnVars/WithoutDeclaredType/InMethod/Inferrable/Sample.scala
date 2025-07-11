package testfiles.DefnVars.WithoutDeclaredType.InMethod.Inferrable

import testfilesext.SampleObject

class Sample {
  def foo(): Unit = {
    var x = SampleObject
  }
}