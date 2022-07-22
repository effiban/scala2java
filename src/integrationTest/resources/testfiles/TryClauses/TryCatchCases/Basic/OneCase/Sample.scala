package dummy

class Sample {

  def foo: Unit = {
    try {
      doSomething()
    } catch {
      case e: Throwable => handleError(e)
    }
  }
}
