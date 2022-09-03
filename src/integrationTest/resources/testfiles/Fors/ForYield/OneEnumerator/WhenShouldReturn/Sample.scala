package dummy

class Sample {

  def foo(): Int = {
    for {
      x <- xs
    } yield doSomething(x)
  }
}
