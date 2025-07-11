package io.github.effiban.scala2java.core.reflection

class TestClassWithDataMembersOnly {
  val x: Int = 3
  val y: (Int, String) = (4, "5")
  val z: (Int, Long, String) => String = (i, l, s) => i.toString + l.toString + s
}