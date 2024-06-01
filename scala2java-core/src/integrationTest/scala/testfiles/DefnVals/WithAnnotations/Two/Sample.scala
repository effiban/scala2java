package testfiles.DefnVals.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

trait Sample {

  @SampleAnnot
  @SampleAnnot2
  val x: Int = 3
}