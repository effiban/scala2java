package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.TermSelectContext
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.ApplyType

trait ApplyTypeTraverser extends ScalaTreeTraverser[ApplyType]

private[traversers] class ApplyTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                 termSelectTraverser: => TermSelectTraverser,
                                                 typeListTraverser: => TypeListTraverser,
                                                 termTraverser: => TermTraverser)
                                                (implicit javaWriter: JavaWriter) extends ApplyTypeTraverser {

  import javaWriter._

  // parametrized type application, e.g.: classOf[X], identity[X]
  override def traverse(termApplyType: ApplyType): Unit = {
    termApplyType.fun match {
      case Term.Name("classOf") =>
        termApplyType.targs match {
          case arg :: _ =>
            typeTraverser.traverse(arg)
            write(".class")
          case _ => write(s"UNPARSEABLE class type: $termApplyType")
        }
      case termSelect: Term.Select => termSelectTraverser.traverse(termSelect, TermSelectContext(termApplyType.targs))
      case term =>
        // In Java a type can only be applied to a qualified name, so the best we can do is guess the qualifier in a comment
        writeComment("this?")
        writeQualifierSeparator()
        typeListTraverser.traverse(termApplyType.targs)
        termTraverser.traverse(term)
    }
  }
}
