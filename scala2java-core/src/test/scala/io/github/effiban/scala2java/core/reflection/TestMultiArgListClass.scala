package io.github.effiban.scala2java.core.reflection

private[reflection] class TestMultiArgListClass {

  def fun1(a: String, b: Int)(c: String, d: Long): String = a + b + c + d

  def fun2(a: String, b: Int)
          (c: String, d: Long)
          (e: String, f: Short): String = a + b + c + d + e + f
}