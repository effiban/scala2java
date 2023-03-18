package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.transformers.TermApplyTypeToTermApplyTransformer

import scala.meta.Term
import scala.meta.Term.ApplyType

trait ApplyTypeTraverser extends ScalaTreeTraverser[ApplyType]

private[traversers] class ApplyTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                 termSelectTraverser: => TermSelectTraverser,
                                                 typeListTraverser: => TypeListTraverser,
                                                 termTraverser: => TermTraverser,
                                                 termApplyTraverser: => TermApplyTraverser,
                                                 termApplyTypeToTermTransformer: TermApplyTypeToTermApplyTransformer)
                                                (implicit javaWriter: JavaWriter) extends ApplyTypeTraverser {

  import javaWriter._

  // parametrized type application, e.g.: classOf[X], identity[X]
  override def traverse(termApplyType: ApplyType): Unit = {
    termApplyTypeToTermTransformer.transform(termApplyType) match {
      case Some(termApply) => termApplyTraverser.traverse(termApply)
      case None => traverseOriginal(termApplyType)
    }
  }

  private def traverseOriginal(termApplyType: ApplyType): Unit = termApplyType.fun match {
    case Term.Name("classOf") => traverseClassOf(termApplyType)
    case termSelect: Term.Select => termSelectTraverser.traverse(termSelect, TermSelectContext(termApplyType.targs))
    case term => traverseUnqualified(termApplyType, term)
  }

  private def traverseClassOf(termApplyType: ApplyType): Unit = {
    termApplyType.targs match {
      case arg :: _ =>
        typeTraverser.traverse(arg)
        write(".class")
      case _ => write(s"UNPARSEABLE class type: $termApplyType")
    }
  }

  private def traverseUnqualified(termApplyType: ApplyType, term: Term): Unit = {
    // In Java a type can only be applied to a qualified name, so the best we can do is guess the qualifier in a comment
    writeComment("this?")
    writeQualifierSeparator()
    typeListTraverser.traverse(termApplyType.targs)
    termTraverser.traverse(term)
  }
}
