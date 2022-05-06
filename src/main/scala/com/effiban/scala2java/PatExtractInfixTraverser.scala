package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Pat

trait PatExtractInfixTraverser extends ScalaTreeTraverser[Pat.ExtractInfix]

class PatExtractInfixTraverserImpl(javaEmitter: JavaEmitter) extends PatExtractInfixTraverser {

  // Pattern match extractor infix e.g. a E b.
  // No Java equivalent (but consider rewriting as a guard ?)
  override def traverse(patternExtractorInfix: Pat.ExtractInfix): Unit = {
    emitComment(s"${patternExtractorInfix.lhs} ${patternExtractorInfix.op} ${patternExtractorInfix.rhs}")
  }
}

object PatExtractInfixTraverser extends PatExtractInfixTraverserImpl(JavaEmitter)
