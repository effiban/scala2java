package testfiles.DefnDefs.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

trait Sample {

  @SampleAnnot(name = "myName", size = 10)
  def foo(): Unit = {}
}