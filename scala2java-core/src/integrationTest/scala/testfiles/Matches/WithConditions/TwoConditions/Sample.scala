package testfiles.Matches.WithConditions.TwoConditions

class Sample {

  def foo(obj: Any): Unit = {
    obj match {
      case x: Int if x > 3 && 5 > 4 => println("matches")
    }
  }
}
