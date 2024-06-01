package testfiles.DefnDefs.WithoutDeclaredType.Inferrable.Match

class Sample {
  private def foo(str: String) = {
    str match {
      case "one" => 1
      case "two" => 2
      case _ => 0
    }
  }
}