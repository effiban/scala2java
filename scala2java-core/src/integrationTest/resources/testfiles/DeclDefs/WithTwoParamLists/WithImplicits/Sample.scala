package dummy

trait Sample {
  def foo(param1: String, param2: Int)
         (implicit param3: String, param4: Int): Unit
}