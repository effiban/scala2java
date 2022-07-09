package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait StatTraverser extends ScalaTreeTraverser[Stat]

private[traversers] class StatTraverserImpl(termTraverser: => TermTraverser,
                                            importTraverser: => ImportTraverser,
                                            pkgTraverser: => PkgTraverser,
                                            defnTraverser: => DefnTraverser,
                                            declTraverser: => DeclTraverser)
                                           (implicit javaWriter: JavaWriter) extends StatTraverser {

  import javaWriter._

  override def traverse(stat: Stat): Unit = stat match {
    case term: Term => termTraverser.traverse(term)
    case `import`: Import => importTraverser.traverse(`import`)
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn)
    case decl: Decl => declTraverser.traverse(decl)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
