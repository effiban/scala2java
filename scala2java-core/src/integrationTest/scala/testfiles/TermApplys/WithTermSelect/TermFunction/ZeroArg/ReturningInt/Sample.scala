package testfiles.TermApplys.WithTermSelect.TermFunction.ZeroArg.ReturningInt

class Sample {
  def foo(): Unit = {
    (() => 3)()
  }
}