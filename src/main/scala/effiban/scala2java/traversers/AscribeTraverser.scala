package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Ascribe

trait AscribeTraverser extends ScalaTreeTraverser[Ascribe]

private[scala2java] class AscribeTraverserImpl(typeTraverser: => TypeTraverser,
                                               termTraverser: => TermTraverser)
                                              (implicit javaWriter: JavaWriter) extends AscribeTraverser {

  import javaWriter._

  // Explicitly specified type, e.g.: x = 2:Short
  // Java equivalent is casting. e.g. x = (short)2
  override def traverse(ascribe: Ascribe): Unit = {
    writeStartDelimiter(Parentheses)
    typeTraverser.traverse(ascribe.tpe)
    writeEndDelimiter(Parentheses)
    termTraverser.traverse(ascribe.expr)
  }
}

object AscribeTraverser extends AscribeTraverserImpl(TypeTraverser, TermTraverser)
