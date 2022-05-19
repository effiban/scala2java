package com.effiban.scala2java.stubs

import com.effiban.scala2java.{DeclValTraverser, JavaEmitter}

import scala.meta.{Decl, Type}

class StubDeclValTraverser(implicit javaEmitter: JavaEmitter) extends DeclValTraverser {
  import javaEmitter._

  override def traverse(valDecl: Decl.Val): Unit = {
    val tpe = valDecl.decltpe match {
      case Type.Name("Int") => "int"
      case Type.Name("String") => "String"
      case aType => throw new IllegalStateException(s"StubDeclValTraverser does not support the type $aType")
    }
    val name = valDecl.pats match {
      case List(nm) => nm
      case _ => throw new IllegalStateException(s"StubDeclValTraverser does not support multiple Pats")
    }
    emit(s"$tpe $name")
  }
}
