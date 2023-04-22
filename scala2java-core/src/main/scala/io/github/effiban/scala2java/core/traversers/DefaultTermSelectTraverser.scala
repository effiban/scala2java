package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait DefaultTermSelectTraverser {
  def traverse(termSelect: Term.Select): Unit
}

private[traversers] class DefaultTermSelectTraverserImpl(qualifierTraverser: => TermTraverser,
                                                         termNameRenderer: TermNameRenderer)
                                                        (implicit javaWriter: JavaWriter) extends DefaultTermSelectTraverser {

  import javaWriter._

  // A qualified name in a stable, non-expression and non-function context
  override def traverse(select: Term.Select): Unit = {
    qualifierTraverser.traverse(select.qual)
    writeQualifierSeparator()
    termNameRenderer.render(select.name)
  }
}
