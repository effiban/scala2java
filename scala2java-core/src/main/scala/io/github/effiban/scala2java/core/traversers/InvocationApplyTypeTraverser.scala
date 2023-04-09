package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.ApplyType

private[traversers] class InvocationApplyTypeTraverser(termSelectTraverser: => TermSelectTraverser,
                                                       typeListTraverser: => TypeListTraverser,
                                                       defaultTermTraverser: => TermTraverser)
                                                      (implicit javaWriter: JavaWriter) extends ApplyTypeTraverser {

  import javaWriter._

  // parametrized type application which is an implicit method invocation, e.g.: identity[X]
  override def traverse(termApplyType: ApplyType): Unit = termApplyType.fun match {
    case termSelect: Term.Select => termSelectTraverser.traverse(termSelect, TermSelectContext(termApplyType.targs))
    case term => traverseUnqualified(termApplyType, term)
  }

  private def traverseUnqualified(termApplyType: ApplyType, term: Term): Unit = {
    // In Java a type can only be applied to a qualified name, so the best we can do is guess the qualifier in a comment
    writeComment("this?")
    writeQualifierSeparator()
    typeListTraverser.traverse(termApplyType.targs)
    defaultTermTraverser.traverse(term)
  }
}
