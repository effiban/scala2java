package testfiles.Matches.WithImplicitReturn

class Sample {

  def foo(str: String): Int = {
    str match {
      case "one" => 1
      case "two" => 2
    }
  }
}
