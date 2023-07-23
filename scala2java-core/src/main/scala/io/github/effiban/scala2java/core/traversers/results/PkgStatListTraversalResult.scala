package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.SealedHierarchies

case class PkgStatListTraversalResult(statResults: List[PopulatedStatTraversalResult] = Nil,
                                      sealedHierarchies: SealedHierarchies = SealedHierarchies())
