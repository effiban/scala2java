package dummy

class Sample {

  def foo: Unit = {
    str match {
      case "one" => println(1)
      case "two" => println(2)
    }
  }
}
