package testfiles.DeclVals.WithTypeParams.WithWildcardAndBound

import testfilesext.SampleTypedClass

abstract class Sample {
  val x: SampleTypedClass[_ <: String]
}