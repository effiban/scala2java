package com.effiban.scala2java.stubs

import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.{JavaEmitter, TermParamListTraverser}

import scala.meta.{Term, Type}

class StubTermParamListTraverser(implicit javaEmitter: JavaEmitter) extends TermParamListTraverser {
  import javaEmitter._

  override def traverse(termParams: List[Term.Param]): Unit = {
    val paramStrings = termParams.map(paramToString)
    emit(s"(${paramStrings.mkString(", ")})")
  }

  private def paramToString(param: Term.Param): String = {
    param.decltpe match {
      case Some(TypeNames.Int) => s"int ${param.name}"
      case Some(Type.Name("String")) => s"String ${param.name}"
      case Some(tpe) => throw new IllegalStateException(s"StubTermParamListTraverser does not support the type $tpe")
      case None => param.toString()
    }
  }
}
