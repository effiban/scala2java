package dummy

class Sample {

  def foo: Unit = {
    try {
      doSomething()
    } finally {
      cleanUp()
    }
  }
}
