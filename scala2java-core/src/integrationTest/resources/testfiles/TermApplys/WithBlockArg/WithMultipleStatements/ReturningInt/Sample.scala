package dummy

class Sample {
  def foo: Unit = {
    doSomething {
      doFirst()
      doSecond()
      3
    }
  }
}