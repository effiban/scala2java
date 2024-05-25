package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.{TermApplyInfixToTermApplyTransformer, TypeSelectTransformer}

import scala.meta.{Import, Pkg, Term, Transformer, Tree, Type}

trait TreeTransformer {
  def transform(tree: Tree): Tree
}

private[transformers] class TreeTransformerImpl(pkgTransformer: => PkgTransformer,
                                                internalTermApplyInfixTransformer: => InternalTermApplyInfixTransformer,
                                                internalTermApplyTransformer: => InternalTermApplyTransformer,
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
        case termApplyInfix: Term.ApplyInfix => internalTermApplyInfixTransformer.transform(termApplyInfix)
        case termApply: Term.Apply => internalTermApplyTransformer.transform(termApply)
        case termSelect: Term.Select => internalTermSelectTransformer.transform(termSelect)
        case typeSelect: Type.Select => typeSelectTransformer.transform(typeSelect).getOrElse(super.apply(typeSelect))
        case aTree => super.apply(aTree)
      }
  }
}
