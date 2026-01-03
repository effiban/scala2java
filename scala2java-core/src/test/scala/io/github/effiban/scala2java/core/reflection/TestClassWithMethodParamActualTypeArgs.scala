package io.github.effiban.scala2java.core.reflection

private[reflection] sealed class TestClassWithMethodParamActualTypeArgs {

  def fun1(a: List[Int]): String = a.toString()

  def fun2(a: Map[Int, Long]): String = a.toString()

  def fun3(a: List[List[Int]]): String = a.toString()

  def fun4(a: List[(Int, Long)]): String = a.toString()

  def fun5(a: (List[Int], Set[Long])): String = a.toString()

  def fun6(a: => List[Int]): String = a.toString()

  def fun7(a: List[Int]*): String = a.toString()

  private class TestInnerClass {
    val x = 5
  }
}

