package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatInterpolateTraverser}

import scala.meta.Pat

class StubPatInterpolateTraverser(implicit javaEmitter: JavaEmitter) extends PatInterpolateTraverser {
  import javaEmitter._

  override def traverse(patInterpolate: Pat.Interpolate): Unit = emitComment(patInterpolate.toString())
}
