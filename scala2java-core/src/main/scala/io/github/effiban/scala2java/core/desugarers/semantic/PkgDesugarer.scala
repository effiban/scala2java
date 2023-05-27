package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Pkg

trait PkgDesugarer extends SameTypeDesugarer[Pkg]

private[semantic] class PkgDesugarerImpl(statDesugarer: => StatDesugarer) extends PkgDesugarer {

  override def desugar(pkg: Pkg): Pkg = {
    val desugaredStats = pkg.stats.map(statDesugarer.desugar)
    pkg.copy(stats = desugaredStats)
  }
}
