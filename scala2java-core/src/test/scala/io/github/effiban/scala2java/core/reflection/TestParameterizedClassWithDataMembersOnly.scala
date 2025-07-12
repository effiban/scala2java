package io.github.effiban.scala2java.core.reflection

sealed class TestParameterizedClassWithDataMembersOnly[A, B, C] {
  var x: A = _
  var y: (A, B) = _
  var z: (A, B, C) => C = _
  var w: List[A] = _
  var v: List[List[A]] = _
}

class TestChildParameterizedClassWithDataMembersOnly[A2, B2, C2] extends TestParameterizedClassWithDataMembersOnly[A2, B2, C2]
