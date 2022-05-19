package com.effiban.scala2java.stubs

import com.effiban.scala2java.{DefnDefTraverser, JavaEmitter}

import scala.meta.{Defn, Type}

class StubDefnDefTraverser(implicit javaEmitter: JavaEmitter) extends DefnDefTraverser {
  import javaEmitter._

  override def traverse(defDefn: Defn.Def): Unit = {
    defDefn.decltpe match {
      case Some(Type.Name("Int")) => emit("int")
      case Some(Type.Name("String")) => emit("String")
      case Some(aType) => throw new IllegalStateException(s"StubDefnDefTraverser does not support the return type $aType")
      case None => emitComment("UnknownType")
    }
    emit(s" ${defDefn.name}()")
    emitBlockStart()
    emitLine(defDefn.body.toString())
    emitBlockEnd()
  }
}
