package testfiles.Matches.WithDefaultCase

class Sample {

  def foo(str: String): Unit = {
    str match {
      case "one" => println(1)
      case _ => println("other")
    }
  }
}
