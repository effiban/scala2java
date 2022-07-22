package dummy

class Sample {

  def foo: Unit = {
    obj match {
      case i : Int => i + 1
      case d : Double => d * 0.5
    }
  }
}
