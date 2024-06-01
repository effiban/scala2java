package testfiles.TermApplys.WithTermSelect.TermFunction.OneArg.IntToUnit

class Sample {
  def foo(): Unit = {
    ((x: Int) => print(x))(3)
  }
}