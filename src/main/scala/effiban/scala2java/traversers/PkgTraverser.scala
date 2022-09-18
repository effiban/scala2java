package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.JavaScope
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

    // TODO handle specific scenarios  of multiple top-level definitions which are illegal in Java (such as 2 public classes in the same file)
    pkg.stats.foreach(stat => statTraverser.traverse(stat, StatContext(JavaScope.Package)))
  }
}
