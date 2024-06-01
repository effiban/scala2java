package testfiles.DefnDefs.WithSingleParamList.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

class Sample {

  @SampleAnnot(name = "myName", size = 10)
  def foo(param1: String, param2: Int): Unit = {
  }
}