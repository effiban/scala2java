package com.effiban.scala2java.stubs

import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.{DefnVarTraverser, JavaEmitter}

import scala.meta.{Defn, Type}

class StubDefnVarTraverser(implicit javaEmitter: JavaEmitter) extends DefnVarTraverser {
  import javaEmitter._

  override def traverse(varDefn: Defn.Var): Unit = {
    val tpe = varDefn.decltpe match {
      case Some(TypeNames.Int) => "int"
      case Some(Type.Name("String")) => "String"
      case Some(aType) => throw new IllegalStateException(s"StubDefnVarTraverser does not support the type $aType")
      case None =>
    }
    val name = varDefn.pats match {
      case List(nm) => nm
      case _ => throw new IllegalStateException(s"StubDefnVarTraverser does not support multiple Pats")
    }
    val rhs = varDefn.rhs match {
      case Some(anRhs) => s" = $anRhs"
      case None => ""
    }
    emit(s"$tpe $name$rhs")
  }
}
