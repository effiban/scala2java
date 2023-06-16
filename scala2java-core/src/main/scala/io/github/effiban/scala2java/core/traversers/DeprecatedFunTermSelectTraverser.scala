package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.renderers.{TermNameRenderer, TypeListRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

@deprecated
trait DeprecatedFunTermSelectTraverser {
  def traverse(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

@deprecated
private[traversers] class DeprecatedFunTermSelectTraverserImpl(qualifierTraverser: => DeprecatedTermTraverser,
                                                               termNameRenderer: TermNameRenderer,
                                                               typeTraverser: => TypeTraverser,
                                                               typeListRenderer: => TypeListRenderer)
                                                              (implicit javaWriter: JavaWriter) extends DeprecatedFunTermSelectTraverser {

  import javaWriter._

  // qualified name which is part of a method invocation, but after any desugaring/transformation have been performed
  override def traverse(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    traverseQualifier(select.qual)
    writeQualifierSeparator(select.qual)
    val traversedTypeArgs = context.appliedTypeArgs.map(typeTraverser.traverse)
    typeListRenderer.render(traversedTypeArgs)
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
