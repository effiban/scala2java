package testfiles.Ifs.IfElse.InRHS

class Sample {

  def foo(): Unit = {
    val parity = if (5 % 2 == 0) "even" else "odd"
  }
}
