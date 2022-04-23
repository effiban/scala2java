package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitLine, emitStatementEnd}

import scala.meta.Pkg

object PkgTraverser extends ScalaTreeTraverser[Pkg] {

  override def traverse(pkg: Pkg): Unit = {
    emit("package ")
    TermRefTraverser.traverse(pkg.ref)
    emitStatementEnd()
    emitLine()
    pkg.stats.foreach(StatTraverser.traverse)
  }
}
