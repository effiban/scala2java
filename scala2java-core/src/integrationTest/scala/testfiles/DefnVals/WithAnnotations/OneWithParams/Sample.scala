package testfiles.DefnVals.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

trait Sample {

  @SampleAnnot(name = "myName", size = 10)
  val x: Int = 3
}