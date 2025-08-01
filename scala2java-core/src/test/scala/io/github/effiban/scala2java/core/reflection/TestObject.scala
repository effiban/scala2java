package io.github.effiban.scala2java.core.reflection

private[reflection] object TestObject {
  val x: Int = 3

  def fun1: Int = 3

  def fun2(): Int = 3

  def fun3(): (Int, String) = (2, "A")

  def fun4(): (Int, Long, String) => String = (i, l, s) => (i + l).toString + s

  def fun5(): List[Int] = List(3)

  def fun6(a: Int, b: Long): String = (a + b).toString
}
