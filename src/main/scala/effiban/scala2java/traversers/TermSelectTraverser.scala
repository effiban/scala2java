package effiban.scala2java.traversers

import effiban.scala2java.transformers.ScalaToJavaTermSelectTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Select

trait TermSelectTraverser extends ScalaTreeTraverser[Term.Select]

private[traversers] class TermSelectTraverserImpl(termTraverser: => TermTraverser,
                                                  termNameTraverser: => TermNameTraverser,
                                                  scalaToJavaTermSelectTransformer: ScalaToJavaTermSelectTransformer)
                                                 (implicit javaWriter: JavaWriter) extends TermSelectTraverser {

  import javaWriter._

  // qualified name
  override def traverse(termSelect: Term.Select): Unit = {
    termSelect match {
      case Select(Term.Name("scala"), name) => termNameTraverser.traverse(name)
      case select =>
        val javaSelect = scalaToJavaTermSelectTransformer.transform(select)
        termTraverser.traverse(javaSelect.qual)
        write(".")
        termNameTraverser.traverse(javaSelect.name)
    }
  }

}
