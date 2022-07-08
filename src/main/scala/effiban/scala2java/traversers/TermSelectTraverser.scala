package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Select

trait TermSelectTraverser extends ScalaTreeTraverser[Term.Select]

private[scala2java] class TermSelectTraverserImpl(termTraverser: => TermTraverser,
                                                  termNameTraverser: => TermNameTraverser)
                                                 (implicit javaWriter: JavaWriter) extends TermSelectTraverser {

  import javaWriter._

  // qualified name
  override def traverse(termSelect: Term.Select): Unit = {
    termSelect match {
      case Select(Term.Name("scala"), name) => termNameTraverser.traverse(name)
      case select =>
        termTraverser.traverse(select.qual)
        write(".")
        termNameTraverser.traverse(select.name)
    }
  }

}

object TermSelectTraverser extends TermSelectTraverserImpl(
  TermTraverser,
  TermNameTraverser
)
