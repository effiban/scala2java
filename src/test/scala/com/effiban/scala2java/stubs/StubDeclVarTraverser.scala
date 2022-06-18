package com.effiban.scala2java.stubs

import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.{DeclVarTraverser, JavaEmitter}

import scala.meta.{Decl, Type}

class StubDeclVarTraverser(implicit javaEmitter: JavaEmitter) extends DeclVarTraverser {
  import javaEmitter._

  override def traverse(varDecl: Decl.Var): Unit = {
    val tpe = varDecl.decltpe match {
      case TypeNames.Int => "int"
      case Type.Name("String") => "String"
      case aType => throw new IllegalStateException(s"StubDeclVarTraverser does not support the type $aType")
    }
    val name = varDecl.pats match {
      case List(nm) => nm
      case _ => throw new IllegalStateException(s"StubDeclVarTraverser does not support multiple Pats")
    }
    emit(s"$tpe $name")
  }
}
