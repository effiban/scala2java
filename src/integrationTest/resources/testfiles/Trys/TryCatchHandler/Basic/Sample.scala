package dummy

class Sample {

  def foo: Unit = {
    try {
      doSomething()
    } catch handleError
  }
}
