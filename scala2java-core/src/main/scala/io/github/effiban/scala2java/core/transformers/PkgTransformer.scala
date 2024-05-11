package io.github.effiban.scala2java.core.transformers

import scala.meta.{Pkg, Stat}

trait PkgTransformer {
  def transform(pkg: Pkg): Pkg
}

private[transformers] class PkgTransformerImpl(treeTransformer: => TreeTransformer) extends PkgTransformer {

  override def transform(pkg: Pkg): Pkg = {
    pkg.copy(stats = pkg.stats.map(treeTransformer.transform(_).asInstanceOf[Stat]))
  }
}
