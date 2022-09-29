package dummy

class Sample {
  def foo: Unit = {
    Right[Err, Int](1)
  }
}