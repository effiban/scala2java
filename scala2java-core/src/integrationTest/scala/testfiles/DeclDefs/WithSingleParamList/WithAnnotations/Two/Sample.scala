package testfiles.DeclDefs.WithSingleParamList.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

trait Sample {

  def foo(@SampleAnnot @SampleAnnot2 param1: String, param2: Int): Unit
}