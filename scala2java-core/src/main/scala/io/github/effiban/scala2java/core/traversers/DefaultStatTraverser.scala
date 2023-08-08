package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait DefaultStatTraverser {
  def traverse(stat: Stat, statContext: StatContext = StatContext()): Option[Stat]
}

private[traversers] class DefaultStatTraverserImpl(statTermTraverser: => StatTermTraverser,
                                                   importTraverser: => ImportTraverser,
                                                   pkgTraverser: => PkgTraverser,
                                                   defnTraverser: => DefnTraverser,
                                                   declTraverser: => DeclTraverser) extends DefaultStatTraverser {

  override def traverse(stat: Stat, statContext: StatContext = StatContext()): Option[Stat] = stat match {
    case term: Term => Some(statTermTraverser.traverse(term))
    case `import`: Import => importTraverser.traverse(`import`)
    case pkg: Pkg => Some(pkgTraverser.traverse(pkg))
    case defn: Defn => Some(defnTraverser.traverse(defn, statContext))
    case decl: Decl => Some(declTraverser.traverse(decl))
    case other => Some(other)
  }
}
