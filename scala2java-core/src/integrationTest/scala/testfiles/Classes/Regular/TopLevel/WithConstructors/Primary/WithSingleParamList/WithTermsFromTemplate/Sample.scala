package testfiles.Classes.Regular.TopLevel.WithConstructors.Primary.WithSingleParamList.WithTermsFromTemplate

import testfilesext.SampleObject

class Sample(param1: Int, param2: Int) {
  SampleObject.func3(param1)
  SampleObject.func3(param2)
}