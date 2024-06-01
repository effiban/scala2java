package testfiles.Matches.Basic.ByString

class Sample {

  def foo(str: String): Unit = {
    str match {
      case "one" => println(1)
      case "two" => println(2)
    }
  }
}
