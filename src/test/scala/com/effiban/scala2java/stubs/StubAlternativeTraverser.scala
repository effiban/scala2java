package com.effiban.scala2java.stubs

import com.effiban.scala2java.{AlternativeTraverser, JavaEmitter}

import scala.meta.Pat.Alternative

class StubAlternativeTraverser(implicit javaEmitter: JavaEmitter) extends AlternativeTraverser {
  import javaEmitter._

  override def traverse(patternAlternative: Alternative): Unit = emit(s"${patternAlternative.lhs}, ${patternAlternative.rhs}")
}
