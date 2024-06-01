package testfiles.DeclDefs.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

trait Sample {

  @SampleAnnot
  @SampleAnnot2
  def foo(): Unit
}