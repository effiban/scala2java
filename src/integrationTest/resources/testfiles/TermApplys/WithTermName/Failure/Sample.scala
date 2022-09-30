package dummy

class Sample {
  def foo: Unit = {
    Failure(new RuntimeException())
  }
}