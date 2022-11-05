package dummy

class Sample {
  def foo: Unit = {
    Future.failed(new RuntimeException())
  }
}