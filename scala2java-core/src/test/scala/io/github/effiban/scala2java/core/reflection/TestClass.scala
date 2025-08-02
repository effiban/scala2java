package io.github.effiban.scala2java.core.reflection

private[reflection] class TestClass {
  val x: Int = 3
  val y: (Int, String) = (4, "5")
  val z: (Int, Long, String) => String = (i, l, s) => i.toString + l.toString + s
  val w: List[Long] = List(1L, 2L, 3L)

  def fun1: Int = 3

  def fun2(): Int = 3

  def fun3(): (Int, String) = (2, "A")

  def fun4(): (Int, Long, String) => String = (i, l, s) => (i + l).toString + s

  def fun5(): List[Int] = List(3)

  def fun6(a: Int, b: Long): String = (a + b).toString

  def fun7(a: AnyVal): String = a.toString

  def fun8(a: (Int, Long)): String = a.toString

  def fun9(a: () => Int): Int = a()

  def fun10(a: (Int, Long) => String): String = a(1, 2L)

  def fun11(a: => Int): String = a.toString

  private class TestInnerClass {
    val x = 5
  }
}