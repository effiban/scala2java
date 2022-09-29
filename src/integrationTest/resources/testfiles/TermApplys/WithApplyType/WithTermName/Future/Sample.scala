package dummy

class Sample {
  def foo: Unit = {
    Future[Int](1)
  }
}