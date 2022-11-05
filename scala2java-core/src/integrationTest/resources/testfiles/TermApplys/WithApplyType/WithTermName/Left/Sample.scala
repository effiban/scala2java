package dummy

class Sample {
  def foo: Unit = {
    Left[Err, Int]("error")
  }
}