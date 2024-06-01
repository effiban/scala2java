package testfiles.DeclVars.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

trait Sample {

  @SampleAnnot
  @SampleAnnot2
  var x: Int
}