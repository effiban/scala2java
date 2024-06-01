package testfiles.DefnDefs.WithSingleParamList.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

class Sample {

  @SampleAnnot
  @SampleAnnot2
  def foo(param1: String, param2: Int): Unit = {
  }
}