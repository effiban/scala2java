package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

trait PatExtractTraverser extends ScalaTreeTraverser[Pat.Extract]

class PatExtractTraverserImpl(javaEmitter: JavaEmitter) extends PatExtractTraverser {

  // Pattern match extractor e.g. A(a, b).
  // No Java equivalent (but consider rewriting as a guard ?)
  override def traverse(patternExtractor: Pat.Extract): Unit = {
    emitComment(s"${patternExtractor.fun}(${patternExtractor.args})")
  }
}

object PatExtractTraverser extends PatExtractTraverserImpl(JavaEmitter)
