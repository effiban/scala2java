package testfiles.Classes.Regular.TopLevel.WithConstructors.Primary.WithSingleParamList.WithAnnotations.OneWithParams

import testfilesext.SampleAnnot

class Sample(@SampleAnnot(name = "myName", size = 10) param1: String, param2: Int)