package dummy

class Sample {

  def foo: Unit = {
    obj match {
      case _ : Int => println("integer")
      case _ : Double => println("double")
    }
  }
}
