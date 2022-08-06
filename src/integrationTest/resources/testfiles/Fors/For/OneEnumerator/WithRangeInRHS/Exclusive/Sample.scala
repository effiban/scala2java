package dummy

class Sample {

  def foo(): Unit = {
    for (i <- 0 until 4) {
      doSomething(i)
    }
  }
}
