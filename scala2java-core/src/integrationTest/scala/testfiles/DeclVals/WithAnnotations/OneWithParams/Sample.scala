package testfiles.DeclVals.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

trait Sample {

  @SampleAnnot(name = "myName", size = 10)
  val x: Int
}