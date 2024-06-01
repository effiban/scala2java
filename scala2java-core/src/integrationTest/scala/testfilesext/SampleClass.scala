package testfilesext

class SampleClass(name: String = "", size: Int = 0) {
  class SampleNestedClass

  def func1(str: String): SampleClass = new SampleClass(str)
  def func2(str: String): SampleClass = new SampleClass(str)
  def func3(str: String): Unit = {}
}
