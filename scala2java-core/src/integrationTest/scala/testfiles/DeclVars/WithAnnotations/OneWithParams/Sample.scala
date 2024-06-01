package testfiles.DeclVars.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

trait Sample {

  @SampleAnnot(name = "myName", size = 10)
  var x: Int
}