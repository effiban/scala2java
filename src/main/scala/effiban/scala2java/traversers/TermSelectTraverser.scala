package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermSelectContext
import effiban.scala2java.transformers.ScalaToJavaTermSelectTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermSelectTraverser {
  def traverse(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

private[traversers] class TermSelectTraverserImpl(termTraverser: => TermTraverser,
                                                  termNameTraverser: => TermNameTraverser,
                                                  typeListTraverser: => TypeListTraverser,
                                                  scalaToJavaTermSelectTransformer: ScalaToJavaTermSelectTransformer)
                                                 (implicit javaWriter: JavaWriter) extends TermSelectTraverser {

  import javaWriter._

  // qualified name
  override def traverse(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    val javaSelect = scalaToJavaTermSelectTransformer.transform(select)
    termTraverser.traverse(javaSelect.qual)
    write(".")
    typeListTraverser.traverse(context.appliedTypeArgs)
    termNameTraverser.traverse(javaSelect.name)
  }
}
