package testfiles.DefnVals.WithNestedType.InClass

import testfilesext.SampleClass

class Sample {
  private val x: SampleClass = new SampleClass()
  private val y: SampleClass#SampleNestedClass = new x.SampleNestedClass()
}