package io.github.effiban.scala2java.core.reflection

private[reflection] sealed class TestParameterizedClass[A, B, C] {
  var x: A = _
  var y: (A, B) = _
  var z: (A, B, C) => C = _
  var w: List[A] = _
  var v: List[List[A]] = _
}

private[reflection] class TestChildParameterizedClass[A2, B2, C2] extends TestParameterizedClass[A2, B2, C2]

private[reflection] class TestChildParameterizedClass2[A2, B2, C2] extends TestParameterizedClass[List[A2], B2, C2]
