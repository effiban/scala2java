package dummy

class Sample {

  def foo(): Unit = {
    for {
      x <- xs
    } yield doSomething(x)
  }
}
