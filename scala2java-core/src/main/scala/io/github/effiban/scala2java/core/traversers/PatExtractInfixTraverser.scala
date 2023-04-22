package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatExtractInfixTraverser extends ScalaTreeTraverser2[Pat.ExtractInfix, Pat.Extract]

object PatExtractInfixTraverser extends PatExtractInfixTraverser {

  /**
   * Pattern match extractor in infix notation, e.g. {{{a MyRecord b}}} in {{{case a MyRecord b =>}}}
   * Rewriting as a [[Pat.Extract]], e.g. {{{MyRecord(a, b)}}} which is closer to Java, although not yet supported either.
   *
   * @see [[PatExtractTraverser]]
   */
  override def traverse(patExtractInfix: Pat.ExtractInfix): Pat.Extract = {
    Pat.Extract(patExtractInfix.op, patExtractInfix.lhs :: patExtractInfix.rhs)
  }
}
