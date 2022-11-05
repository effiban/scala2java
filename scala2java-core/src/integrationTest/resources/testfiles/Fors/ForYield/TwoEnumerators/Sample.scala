package dummy

class Sample {

  def foo(): Unit = {
    for {
      x <- xs
      y <- ys
    } yield doSomething(x, y)
  }
}
