package testfiles.DefnVars.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

trait Sample {

  @SampleAnnot
  @SampleAnnot2
  var x: Int = 3
}