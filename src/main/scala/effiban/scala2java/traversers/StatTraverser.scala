package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait StatTraverser {
  def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit
}

private[traversers] class StatTraverserImpl(termTraverser: => TermTraverser,
                                            importTraverser: => ImportTraverser,
                                            pkgTraverser: => PkgTraverser,
                                            defnTraverser: => DefnTraverser,
                                            declTraverser: => DeclTraverser)
                                           (implicit javaWriter: JavaWriter) extends StatTraverser {

  import javaWriter._

  override def traverse(stat: Stat, statContext: StatContext = StatContext()): Unit = stat match {
    case term: Term => termTraverser.traverse(term)
    case `import`: Import => importTraverser.traverse(`import`, statContext)
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn, statContext)
    case decl: Decl => declTraverser.traverse(decl, statContext)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
