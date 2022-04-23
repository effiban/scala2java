package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.traverseGenericTypeList
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
            GenericTreeTraverser.traverse(arg)
            emit(".class")
          case _ => emit(s"UNPARSEABLE class type: $termApplyType")
        }
      case fun =>
        GenericTreeTraverser.traverse(fun)
        if (termApplyType.targs.nonEmpty) {
          emit(".")
        }
        traverseGenericTypeList(termApplyType.targs)
    }
  }
}
