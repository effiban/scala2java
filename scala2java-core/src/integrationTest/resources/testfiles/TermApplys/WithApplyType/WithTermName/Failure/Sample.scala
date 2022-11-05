package dummy

class Sample {
  def foo: Unit = {
    Failure[Int](new RuntimeException())
  }
}