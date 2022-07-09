package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pkg

trait PkgTraverser extends ScalaTreeTraverser[Pkg]

private[traversers] class PkgTraverserImpl(termRefTraverser: => TermRefTraverser,
                                           statTraverser: => StatTraverser)
                                          (implicit javaWriter: JavaWriter) extends PkgTraverser {

  import javaWriter._

  override def traverse(pkg: Pkg): Unit = {
    write("package ")
    termRefTraverser.traverse(pkg.ref)
    writeStatementEnd()
    writeLine()
    pkg.stats.foreach(statTraverser.traverse)
  }
}
