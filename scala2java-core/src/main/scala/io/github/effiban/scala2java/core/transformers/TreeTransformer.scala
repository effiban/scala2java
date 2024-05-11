package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Import, Pkg, Transformer, Tree, Type}

trait TreeTransformer {
  def transform(tree: Tree): Tree
}

private[transformers] class TreeTransformerImpl(pkgTransformer: => PkgTransformer,
                                                typeSelectTransformer: TypeSelectTransformer) extends TreeTransformer {

  override def transform(tree: Tree): Tree = {
    new InnerTransformer()(tree)
  }

  private class InnerTransformer extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case pkg: Pkg => pkgTransformer.transform(pkg)
        case `import`: Import => `import`
        case typeSelect: Type.Select => typeSelectTransformer.transform(typeSelect).getOrElse(super.apply(typeSelect))
        case aTree => super.apply(aTree)
      }
  }
}
