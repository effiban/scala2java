package dummy

trait Sample {

  @MyAnnot(name = "myName", size = 10)
  def foo: Unit
}