package testfiles.Imports.NonPackageScope.ClassScope

class Sample {
  import testfilesext.SampleObject.x
  private val y: Int = x
}
