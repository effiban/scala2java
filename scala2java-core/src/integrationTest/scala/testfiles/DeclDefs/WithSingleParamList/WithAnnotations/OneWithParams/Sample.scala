package testfiles.DeclDefs.WithSingleParamList.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

trait Sample {

  def foo(@SampleAnnot(name = "myName") param1: String, param2: Int): Unit
}