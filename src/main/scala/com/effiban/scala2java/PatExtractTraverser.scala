package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

object PatExtractTraverser extends ScalaTreeTraverser[Pat.Extract] {

  // Pattern match extractor e.g. A(a, b).
  // No Java equivalent (but consider rewriting as a guard ?)
  override def traverse(patternExtractor: Pat.Extract): Unit = {
    emitComment(s"${patternExtractor.fun}(${patternExtractor.args})")
  }
}
