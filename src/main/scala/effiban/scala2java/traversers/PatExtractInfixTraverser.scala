package effiban.scala2java.traversers

import scala.meta.Pat

trait PatExtractInfixTraverser extends ScalaTreeTraverser[Pat.ExtractInfix]

class PatExtractInfixTraverserImpl(patExtractTraverser: => PatExtractTraverser) extends PatExtractInfixTraverser {

  /**
   * Pattern match extractor in infix notation, e.g. {{{a MyRecord b}}}
   * Rewriting as a [[Pat.Extract]], e.g. {{{MyRecord(a, b)}}} which is closer to Java, although not yet supported either.
   *
   * @see [[PatExtractTraverser]]
   */
  override def traverse(patExtractInfix: Pat.ExtractInfix): Unit = {
    val patExtract = Pat.Extract(patExtractInfix.op, patExtractInfix.lhs :: patExtractInfix.rhs)
    patExtractTraverser.traverse(patExtract)
  }
}
