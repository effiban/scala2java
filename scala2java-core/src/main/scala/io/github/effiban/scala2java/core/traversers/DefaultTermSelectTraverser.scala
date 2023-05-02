package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait DefaultTermSelectTraverser extends ScalaTreeTraverser1[Term.Select]

private[traversers] class DefaultTermSelectTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser) extends DefaultTermSelectTraverser {

  // A qualified name in a stable, non-expression and non-function context
  override def traverse(select: Term.Select): Term.Select = {
    val traversedQual = traverseQualifier(select.qual)
    Term.Select(traversedQual, select.name)
  }

  private def traverseQualifier(qual: Term): Term = {
    qual match {
      case aQual: Term.Ref => defaultTermRefTraverser.traverse(aQual)
      case aQual => aQual
    }
  }
}
