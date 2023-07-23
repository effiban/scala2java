package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.SealedHierarchies

import scala.meta.{Pkg, Stat, Term}

case class PkgTraversalResult(pkgRef: Term.Ref,
                              statResults: List[PopulatedStatTraversalResult] = Nil,
                              sealedHierarchies: SealedHierarchies = SealedHierarchies()) extends PopulatedStatTraversalResult {

  override val tree: Stat = Pkg(
    ref = pkgRef,
    stats = statResults.map(_.tree)
  )
}
