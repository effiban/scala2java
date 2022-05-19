package com.effiban.scala2java.stubs

import com.effiban.scala2java.{DeclTypeTraverser, JavaEmitter}

import scala.meta.Decl

class StubDeclTypeTraverser(implicit javaEmitter: JavaEmitter) extends DeclTypeTraverser {
  import javaEmitter._

  override def traverse(typeDecl: Decl.Type): Unit = {
    emit(s"interface ${typeDecl.name.value}")
    emitBlockStart()
    emitBlockEnd()
  }
}
