package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait FunTermSelectTraverser {
  def traverse(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

private[traversers] class FunTermSelectTraverserImpl(qualifierTraverser: => TermTraverser,
                                                     termNameRenderer: TermNameRenderer,
                                                     typeListTraverser: => TypeListTraverser)
                                                    (implicit javaWriter: JavaWriter) extends FunTermSelectTraverser {

  import javaWriter._

  // qualified name which is part of a method invocation, but after any desugaring/transformation have been performed
  override def traverse(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    traverseQualifier(select.qual)
    writeQualifierSeparator(select.qual)
    typeListTraverser.traverse(context.appliedTypeArgs)
    termNameRenderer.render(select.name)
  }

  private def traverseQualifier(qualifier: Term): Unit = {
    qualifier match {
      case qual@(_: Term.Function | Term.Ascribe(_: Term.Function, _)) => traverseInsideParens(qual)
      case qual => qualifierTraverser.traverse(qual)
    }
  }

  private def traverseInsideParens(qual: Term): Unit = {
    writeArgumentsStart(Parentheses)
    qualifierTraverser.traverse(qual)
    writeArgumentsEnd(Parentheses)
  }

  private def writeQualifierSeparator(qualifier: Term): Unit = {
    qualifier match {
      case _: Term.Apply => writeLine()
      case _ =>
    }
    javaWriter.writeQualifierSeparator()
  }
}
