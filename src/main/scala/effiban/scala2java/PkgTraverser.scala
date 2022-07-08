package effiban.scala2java

import scala.meta.Pkg

trait PkgTraverser extends ScalaTreeTraverser[Pkg]

private[scala2java] class PkgTraverserImpl(termRefTraverser: => TermRefTraverser,
                                           statTraverser: => StatTraverser)
                                          (implicit javaEmitter: JavaEmitter) extends PkgTraverser {

  import javaEmitter._

  override def traverse(pkg: Pkg): Unit = {
    emit("package ")
    termRefTraverser.traverse(pkg.ref)
    emitStatementEnd()
    emitLine()
    pkg.stats.foreach(statTraverser.traverse)
  }
}

object PkgTraverser extends PkgTraverserImpl(TermRefTraverser, StatTraverser)
