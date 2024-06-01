package testfiles.Throws.WhenMethodDoesNotReturn

class Sample {

  def foo(): Unit = {
    throw new IllegalStateException("error")
  }
}
