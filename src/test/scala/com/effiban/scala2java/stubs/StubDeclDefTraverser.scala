package com.effiban.scala2java.stubs

import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.{DeclDefTraverser, JavaEmitter}

import scala.meta.{Decl, Type}

class StubDeclDefTraverser(implicit javaEmitter: JavaEmitter) extends DeclDefTraverser {
  import javaEmitter._

  override def traverse(defDecl: Decl.Def): Unit = {
    val tpe = defDecl.decltpe match {
      case TypeNames.Int => "int"
      case Type.Name("String") => "String"
      case aType => throw new IllegalStateException(s"StubDeclDefTraverser does not support the return type $aType")
    }
    emit(s"$tpe ${defDecl.name}()")
  }
}
