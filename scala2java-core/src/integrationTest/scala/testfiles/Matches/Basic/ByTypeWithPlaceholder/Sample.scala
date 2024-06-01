package testfiles.Matches.Basic.ByTypeWithPlaceholder

class Sample {

  def foo(obj: Any): Unit = {
    obj match {
      case _ : Int => println("integer")
      case _ : Double => println("double")
    }
  }
}
