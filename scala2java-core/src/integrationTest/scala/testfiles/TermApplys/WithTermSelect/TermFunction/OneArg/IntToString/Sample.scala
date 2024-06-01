package testfiles.TermApplys.WithTermSelect.TermFunction.OneArg.IntToString

class Sample {
  def foo(): Unit = {
    ((x: Int) => "bla")(3)
  }
}