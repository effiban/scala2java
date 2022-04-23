package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term
import scala.meta.Term.ApplyType

object ApplyTypeTraverser extends ScalaTreeTraverser[ApplyType] {

  // parametrized type application, e.g.: classOf[X], identity[X], List[X]
  override def traverse(termApplyType: ApplyType): Unit = {
    termApplyType.fun match {
      case Term.Name("classOf") =>
        termApplyType.targs match {
          case arg :: _ =>
            TypeTraverser.traverse(arg)
            emit(".class")
          case _ => emit(s"UNPARSEABLE class type: $termApplyType")
        }
      case fun =>
        TermTraverser.traverse(fun)
        if (termApplyType.targs.nonEmpty) {
          emit(".")
        }
        TypeListTraverser.traverse(termApplyType.targs)
    }
  }
}
