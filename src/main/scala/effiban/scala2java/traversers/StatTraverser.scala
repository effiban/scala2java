package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait StatTraverser extends ScalaTreeTraverser[Stat]

private[scala2java] class StatTraverserImpl(termTraverser: => TermTraverser,
                                            importTraverser: => ImportTraverser,
                                            pkgTraverser: => PkgTraverser,
                                            defnTraverser: => DefnTraverser,
                                            declTraverser: => DeclTraverser)
                                           (implicit javaEmitter: JavaEmitter) extends StatTraverser {

  import javaEmitter._

  override def traverse(stat: Stat): Unit = stat match {
    case term: Term => termTraverser.traverse(term)
    case `import`: Import => importTraverser.traverse(`import`)
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn)
    case decl: Decl => declTraverser.traverse(decl)
    case other => emitComment(s"UNSUPPORTED: $other")
  }

}

object StatTraverser extends StatTraverserImpl(
  TermTraverser,
  ImportTraverser,
  PkgTraverser,
  DefnTraverser,
  DeclTraverser
)