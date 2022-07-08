package effiban.scala2java.traversers

import effiban.scala2java.{JavaEmitter, Parentheses}

import scala.meta.Term.Ascribe

trait AscribeTraverser extends ScalaTreeTraverser[Ascribe]

private[scala2java] class AscribeTraverserImpl(typeTraverser: => TypeTraverser,
                                               termTraverser: => TermTraverser)
                                              (implicit javaEmitter: JavaEmitter) extends AscribeTraverser {

  import javaEmitter._

  // Explicitly specified type, e.g.: x = 2:Short
  // Java equivalent is casting. e.g. x = (short)2
  override def traverse(ascribe: Ascribe): Unit = {
    emitStartDelimiter(Parentheses)
    typeTraverser.traverse(ascribe.tpe)
    emitEndDelimiter(Parentheses)
    termTraverser.traverse(ascribe.expr)
  }
}

object AscribeTraverser extends AscribeTraverserImpl(TypeTraverser, TermTraverser)
