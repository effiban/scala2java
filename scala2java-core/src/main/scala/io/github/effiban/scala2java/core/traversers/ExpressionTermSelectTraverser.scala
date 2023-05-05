package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.renderers.{TermNameRenderer, TypeListRenderer}
import io.github.effiban.scala2java.core.transformers.InternalTermSelectTransformer
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext

import scala.meta.Term

trait ExpressionTermSelectTraverser {
  def traverse(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

private[traversers] class ExpressionTermSelectTraverserImpl(qualifierTraverser: => TermTraverser,
                                                            transformedTermTraverser: => TermTraverser,
                                                            termNameRenderer: TermNameRenderer,
                                                            typeTraverser: => TypeTraverser,
                                                            typeListRenderer: => TypeListRenderer,
                                                            qualifierTypeInferrer: => QualifierTypeInferrer,
                                                            termSelectTransformer: InternalTermSelectTransformer)
                                                           (implicit javaWriter: JavaWriter) extends ExpressionTermSelectTraverser {

  import javaWriter._

  // qualified name in the context of an evaluated expression, that might need to be desugared or otherwise transformed into a Java equivalent
  override def traverse(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    val maybeQualType = qualifierTypeInferrer.infer(select)
    val transformedTerm = termSelectTransformer.transform(select, TermSelectTransformationContext(maybeQualType))
    transformedTerm match {
        case transformedSelect: Term.Select => traverseAsSelect(transformedSelect, context)
        case term => transformedTermTraverser.traverse(term)
    }
  }

  private def traverseAsSelect(transformedSelect: Term.Select, context: TermSelectContext): Unit = {
    traverseQualifier(transformedSelect.qual)
    writeQualifierSeparator(transformedSelect.qual)
    val traversedTypeArgs = context.appliedTypeArgs.map(typeTraverser.traverse)
    typeListRenderer.render(traversedTypeArgs)
    termNameRenderer.render(transformedSelect.name)
  }

  private def traverseQualifier(qualifier: Term): Unit = {
    qualifier match {
      case qual@(_: Term.Function | Term.Ascribe(_: Term.Function,_)) => traverseInsideParens(qual)
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
      case _ : Term.Apply => writeLine()
      case _ =>
    }
    javaWriter.writeQualifierSeparator()
  }
}
