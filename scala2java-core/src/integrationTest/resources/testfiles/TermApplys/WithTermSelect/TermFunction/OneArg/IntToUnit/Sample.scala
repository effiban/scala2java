package dummy

class Sample {
  def foo: Unit = {
    ((x: Int) => print(x))(3)
  }
}