package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait StatTraverser extends ScalaTreeTraverser[Stat]

private[scala2java] class StatTraverserImpl(termTraverser: => TermTraverser,
                                            importTraverser: => ImportTraverser,
                                            pkgTraverser: => PkgTraverser,
                                            defnTraverser: => DefnTraverser,
                                            declTraverser: => DeclTraverser) extends StatTraverser {

  override def traverse(stat: Stat): Unit = stat match {
    case term: Term => termTraverser.traverse(term)
    case `import`: Import => importTraverser.traverse(`import`)
    case pkg: Pkg => pkgTraverser.traverse(pkg)
    case defn: Defn => defnTraverser.traverse(defn)
    case decl: Decl => DeclTraverser.traverse(decl)
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