package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

object StatTraverser extends ScalaTreeTraverser[Stat] {

  override def traverse(stat: Stat): Unit = stat match {
    case term: Term => TermTraverser.traverse(term)
    case `import`: Import => ImportTraverser.traverse(`import`)
    case pkg: Pkg => PkgTraverser.traverse(pkg)
    case defn: Defn => DefnTraverser.traverse(defn)
    case decl: Decl => DeclTraverser.traverse(decl)
    case other => emitComment(s"UNSUPPORTED: $other")
  }

}
