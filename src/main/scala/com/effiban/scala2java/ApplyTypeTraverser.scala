package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term
import scala.meta.Term.ApplyType

trait ApplyTypeTraverser extends ScalaTreeTraverser[ApplyType]

private[scala2java] class ApplyTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                 termTraverser: => TermTraverser,
                                                 typeListTraverser: => TypeListTraverser) extends ApplyTypeTraverser {

  // parametrized type application, e.g.: classOf[X], identity[X], List[X]
  override def traverse(termApplyType: ApplyType): Unit = {
    termApplyType.fun match {
      case Term.Name("classOf") =>
        termApplyType.targs match {
          case arg :: _ =>
            typeTraverser.traverse(arg)
            emit(".class")
          case _ => emit(s"UNPARSEABLE class type: $termApplyType")
        }
      case fun =>
        termTraverser.traverse(fun)
        if (termApplyType.targs.nonEmpty) {
          emit(".")
        }
        typeListTraverser.traverse(termApplyType.targs)
    }
  }
}

object ApplyTypeTraverser extends ApplyTypeTraverserImpl(TypeTraverser, TermTraverser, TypeListTraverser)
