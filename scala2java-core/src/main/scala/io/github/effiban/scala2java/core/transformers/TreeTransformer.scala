package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.{TermSelectNameTransformer, TermSelectTransformer, TypeSelectTransformer}

import scala.meta.{Import, Pkg, Term, Transformer, Tree, Type}

trait TreeTransformer {
  def transform(tree: Tree): Tree
}

private[transformers] class TreeTransformerImpl(pkgTransformer: => PkgTransformer,
                                                internalTermSelectTransformer: => InternalTermSelectTransformer,
                                                typeSelectTransformer: TypeSelectTransformer) extends TreeTransformer {

  override def transform(tree: Tree): Tree = {
    new InnerTransformer()(tree)
  }

  private class InnerTransformer extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case pkg: Pkg => pkgTransformer.transform(pkg)
        case `import`: Import => `import`
        case termSelect: Term.Select => internalTermSelectTransformer.transform(termSelect)
        case typeSelect: Type.Select => typeSelectTransformer.transform(typeSelect).getOrElse(super.apply(typeSelect))
        case aTree => super.apply(aTree)
      }
  }
}
