package testfiles.TermApplys.WithApplyType.WithTermName.Left

class Sample {
  def foo(): Unit = {
    Left[Throwable, String](new RuntimeException())
  }
}