package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

trait TermSelectTraverser {
  def traverse(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

private[traversers] class TermSelectTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                  termNameTraverser: => TermNameTraverser,
                                                  typeListTraverser: => TypeListTraverser,
                                                  qualifierTypeInferrer: => QualifierTypeInferrer,
                                                  termSelectTransformer: TermSelectTransformer)
                                                 (implicit javaWriter: JavaWriter) extends TermSelectTraverser {

  import javaWriter._

  // qualified name
  override def traverse(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    val maybeQualType = qualifierTypeInferrer.infer(select)
    val javaSelect = termSelectTransformer.transform(select, TermSelectTransformationContext(maybeQualType))
    traverseQualifier(javaSelect.qual)
    writeQualifierSeparator(javaSelect.qual)
    typeListTraverser.traverse(context.appliedTypeArgs)
    termNameTraverser.traverse(javaSelect.name)
  }

  private def traverseQualifier(qualifier: Term): Unit = {
    qualifier match {
      case qual@(_: Term.Function | Term.Ascribe(_: Term.Function,_)) => traverseInsideParens(qual)
      case qual => expressionTermTraverser.traverse(qual)
    }
  }

  private def traverseInsideParens(qual: Term): Unit = {
    writeArgumentsStart(Parentheses)
    expressionTermTraverser.traverse(qual)
    writeArgumentsEnd(Parentheses)
  }

  private def writeQualifierSeparator(qualifier: Term): Unit = {
    qualifier match {
      case _ : Term.Apply => writeLine()
      case _ =>
    }
    javaWriter.writeQualifierSeparator()
  }
}
