package com.effiban.scala2java.stubs

import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.{DefnValTraverser, JavaEmitter}

import scala.meta.{Defn, Type}

class StubDefnValTraverser(implicit javaEmitter: JavaEmitter) extends DefnValTraverser {
  import javaEmitter._

  override def traverse(valDefn: Defn.Val): Unit = {
    val tpe = valDefn.decltpe match {
      case Some(TypeNames.Int) => "int"
      case Some(Type.Name("String")) => "String"
      case Some(aType) => throw new IllegalStateException(s"StubDefnValTraverser does not support the type $aType")
      case None =>
    }
    val name = valDefn.pats match {
      case List(nm) => nm
      case _ => throw new IllegalStateException(s"StubDefnValTraverser does not support multiple Pats")
    }
    emit(s"$tpe $name = ${valDefn.rhs}")
  }
}
