package testfiles.DefnVals.WithoutDeclaredType.InClass.Inferrable.TermApply.WithThreeArgLists

import testfilesext.SampleObject

class Sample {
  private val x = SampleObject.func16(1, 2L)(3, 4L)(5, 6L)
}