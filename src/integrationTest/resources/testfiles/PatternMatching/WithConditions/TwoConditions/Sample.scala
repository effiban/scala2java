package dummy

class Sample {

  def foo: Unit = {
    obj match {
      case x: Int if x > 3 && y > 4 => println("matches")
    }
  }
}
