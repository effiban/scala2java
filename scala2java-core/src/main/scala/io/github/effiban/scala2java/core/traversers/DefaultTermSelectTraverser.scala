package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait DefaultTermSelectTraverser {
  def traverse(termSelect: Term.Select): Unit
}

private[traversers] class DefaultTermSelectTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                         termNameRenderer: TermNameRenderer)
                                                        (implicit javaWriter: JavaWriter) extends DefaultTermSelectTraverser {

  import javaWriter._

  // A qualified name in a stable, non-expression and non-function context
  override def traverse(select: Term.Select): Unit = {
    traverseQualifier(select.qual)
    writeQualifierSeparator()
    termNameRenderer.render(select.name)
  }

  private def traverseQualifier(qual: Term): Unit = {
    qual match {
      case aQual: Term.Ref => defaultTermRefTraverser.traverse(aQual)
      case aQual => throw new IllegalStateException(s"Invalid qualifier in stable path context: $aQual")
    }
  }
}
