package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.traversers.results._

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term}

trait DefaultStatTraverser {
  def traverse(stat: Stat, statContext: StatContext = StatContext()): StatTraversalResult
}

private[traversers] class DefaultStatTraverserImpl(statTermTraverser: => StatTermTraverser,
                                                   importTraverser: => ImportTraverser,
                                                   pkgTraverser: => PkgTraverser,
                                                   defnTraverser: => DefnTraverser,
                                                   declTraverser: => DeclTraverser) extends DefaultStatTraverser {

  override def traverse(stat: Stat, statContext: StatContext = StatContext()): StatTraversalResult = stat match {
    case term: Term => SimpleStatTraversalResult(statTermTraverser.traverse(term))
    case `import`: Import => traverseImport(`import`)
    case pkg: Pkg => SimpleStatTraversalResult(pkgTraverser.traverse(pkg))
    case defn: Defn => defnTraverser.traverse(defn, statContext)
    case decl: Decl => SimpleStatTraversalResult(declTraverser.traverse(decl, statContext))
    case other => SimpleStatTraversalResult(other)
  }

  private def traverseImport(`import`: Import) = {
    importTraverser.traverse(`import`)
      .map(SimpleStatTraversalResult)
      .getOrElse(EmptyStatTraversalResult)
  }
}
