package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitLine, emitStatementEnd}

import scala.meta.Pkg

trait PkgTraverser extends ScalaTreeTraverser[Pkg]

private[scala2java] class PkgTraverserImpl(termRefTraverser: => TermRefTraverser,
                                           statTraverser: => StatTraverser) extends PkgTraverser {

  override def traverse(pkg: Pkg): Unit = {
    emit("package ")
    termRefTraverser.traverse(pkg.ref)
    emitStatementEnd()
    emitLine()
    pkg.stats.foreach(statTraverser.traverse)
  }
}

object PkgTraverser extends PkgTraverserImpl(TermRefTraverser, StatTraverser)
