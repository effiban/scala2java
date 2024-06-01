package testfiles.TermApplys.WithApplyType.WithTermName.Right

class Sample {
  def foo(): Unit = {
    Right[Throwable, Int](1)
  }
}