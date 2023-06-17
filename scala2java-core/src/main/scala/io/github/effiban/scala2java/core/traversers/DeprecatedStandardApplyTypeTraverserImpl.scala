package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.renderers.TypeListRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.ApplyType

@deprecated
private[traversers] class DeprecatedStandardApplyTypeTraverserImpl(expressionTermSelectTraverser: => DeprecatedExpressionTermSelectTraverser,
                                                                   typeTraverser: => TypeTraverser,
                                                                   typeListRenderer: => TypeListRenderer,
                                                                   unqualifiedTermTraverser: => DeprecatedExpressionTermTraverser)
                                                                  (implicit javaWriter: JavaWriter) extends DeprecatedStandardApplyTypeTraverser {

  import javaWriter._

  // parametrized type application which is the 'fun' of a method invocation (after a possible desugaring and transformation)
  override def traverse(termApplyType: ApplyType): Unit = termApplyType.fun match {
    case termSelect: Term.Select => expressionTermSelectTraverser.traverse(termSelect, TermSelectContext(termApplyType.targs))
    case term => traverseUnqualified(termApplyType, term)
  }

  private def traverseUnqualified(termApplyType: ApplyType, term: Term): Unit = {
    // In Java a type can only be applied to a qualified name, so the best we can do is guess the qualifier in a comment
    writeComment("this?")
    writeQualifierSeparator()
    val traversedTypeArgs = termApplyType.targs.map(typeTraverser.traverse)
    typeListRenderer.render(traversedTypeArgs)
    unqualifiedTermTraverser.traverse(term)
  }
}
