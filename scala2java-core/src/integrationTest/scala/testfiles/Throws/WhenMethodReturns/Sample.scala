package testfiles.Throws.WhenMethodReturns

class Sample {

  def foo(): Int = {
    throw new IllegalStateException("error")
  }
}
