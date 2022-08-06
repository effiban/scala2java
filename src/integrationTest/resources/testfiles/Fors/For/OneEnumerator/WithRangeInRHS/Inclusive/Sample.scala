package dummy

class Sample {

  def foo(): Unit = {
    for (i <- 1 to 4) {
      doSomething(i)
    }
  }
}
