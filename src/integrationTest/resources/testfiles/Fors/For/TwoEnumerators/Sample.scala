package dummy

class Sample {

  def foo(): Unit = {
    for (x <- xs;
         y <- ys) {
      doSomething(x, y)
    }
  }
}
