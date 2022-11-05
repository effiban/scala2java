package dummy

class Sample {

  def foo(): Unit = {
    for {
      x <- xs
      y <- ys
      z <- zs
    } yield doSomething(x, y, z)
  }
}
