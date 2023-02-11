package dummy

class Sample {
  def foo: Unit = {
    (() => 3)()
  }
}