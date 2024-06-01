package testfiles.Matches.WithConditions.OneCondition

class Sample {

  def foo(obj: Any): Unit = {
    obj match {
      case x: Int if x > 3 => println("matches")
    }
  }
}
