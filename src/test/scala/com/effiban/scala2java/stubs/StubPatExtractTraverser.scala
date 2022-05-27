package com.effiban.scala2java.stubs

import com.effiban.scala2java.{JavaEmitter, PatExtractTraverser}

import scala.meta.Pat

class StubPatExtractTraverser(implicit javaEmitter: JavaEmitter) extends PatExtractTraverser {
  import javaEmitter._

  override def traverse(patternExtractor: Pat.Extract): Unit = emitComment(s"${patternExtractor.fun}(${patternExtractor.args.mkString(", ")})")
}
