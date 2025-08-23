package testfilesext

object SampleObject {
  class SampleType

  val x = 3
  val y = 4

  def func1(): Unit = {}
  def func1B: String = ""
  def func2(): Unit = {}
  def func3(x: Int): Unit = {}
  def func4(x: Int, y: Int): Unit = {}
  def func5(x: Int): Int = x
  def func6(x: Int, y: Int, z: Int): Unit = {}
  def func7[T](): Unit = {}
  def func7B[T]: Int = 2
  def func8[T, U](): Unit = {}
  def func9(tuple: (Int, Int, Int)): Unit = {}
  def func10(tuple: (Int, Int)): Unit = {}
  def func11(block: => Int): Unit = {}
  def func12(x1: Int, x2: Int)(y1: Int, y2: Int): Unit = {}
  def func13(e: Throwable): Unit = {}
  def func14(e: Throwable): Unit = {}
  def func15(x1: Int, x2: Long)(y1: Int, y2: Long): String = (x1 + x2 + y1 + y2).toString
  def func16(x1: Int, x2: Long)
            (y1: Int, y2: Long)
            (z1: Int, z2: Long) : String = (x1 + x2 + y1 + y2 + z1 + z2).toString

  def partialFunc: PartialFunction[Throwable, Unit] = { case e => }
}
