package testfiles.Classes.Regular.TopLevel.WithConstructors.Primary.WithSingleParamList.WithAnnotations.Two

import testfilesext.{SampleAnnot, SampleAnnot2}

class Sample(@SampleAnnot() @SampleAnnot2() param1: String, param2: Int)