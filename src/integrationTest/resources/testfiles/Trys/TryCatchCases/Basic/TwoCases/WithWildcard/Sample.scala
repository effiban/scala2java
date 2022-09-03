package dummy

class Sample {

  def foo: Unit = {
    try {
      doSomething()
    } catch {
      case e: IllegalStateException => handleIllegalState(e)
      case _ => handleError()
    }
  }
}
