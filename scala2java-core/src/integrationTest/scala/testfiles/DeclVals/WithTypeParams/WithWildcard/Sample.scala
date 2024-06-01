package testfiles.DeclVals.WithTypeParams.WithWildcard

import testfilesext.SampleTypedClass

abstract class Sample {
  val x: SampleTypedClass[_]
}