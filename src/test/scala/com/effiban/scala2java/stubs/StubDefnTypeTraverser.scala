package com.effiban.scala2java.stubs

import com.effiban.scala2java.{DefnTypeTraverser, JavaEmitter}

import scala.meta.Defn

class StubDefnTypeTraverser(implicit javaEmitter: JavaEmitter) extends DefnTypeTraverser {
  import javaEmitter._

  override def traverse(typeDefn: Defn.Type): Unit = {
    emit(s"interface ${typeDefn.name.value}")
    emitBlockStart()
    emitComment(typeDefn.body.toString())
    emitLine()
    emitBlockEnd()
  }
}
