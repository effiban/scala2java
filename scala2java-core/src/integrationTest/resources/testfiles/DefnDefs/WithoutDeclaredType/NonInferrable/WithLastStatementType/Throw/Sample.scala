package dummy

class Sample {
  def foo = {
    doSomething()
    throw new IllegalStateException()
  }
}