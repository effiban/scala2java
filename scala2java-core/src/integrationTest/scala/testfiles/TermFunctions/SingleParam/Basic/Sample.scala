package testfiles.TermFunctions.SingleParam.Basic

class Sample {
  def foo(): Unit = {
    val f = (x: Int) => println(x)
    f(3)
  }
}