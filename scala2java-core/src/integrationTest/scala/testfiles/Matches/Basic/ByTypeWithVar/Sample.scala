package testfiles.Matches.Basic.ByTypeWithVar

class Sample {

  def foo(obj: Any): Unit = {
    obj match {
      case i : Int => i + 1
      case d : Double => d * 0.5
    }
  }
}
