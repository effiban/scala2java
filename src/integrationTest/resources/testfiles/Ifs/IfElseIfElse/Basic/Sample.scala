package dummy

class Sample {

  def foo(): Unit = {
    if (x < 3) {
      doSomething(x)
    } else if (x < 10) {
      doSomethingElse(x)
    } else {
      doDefault()
    }
  }
}
