package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer

import scala.meta.{Import, Pkg, Template, Term, Transformer, Tree, Type}

trait TreeTransformer {
  def transform(tree: Tree): Tree
}

private[transformers] class TreeTransformerImpl(pkgTransformer: => PkgTransformer,
                                                templateTransformer: => TemplateTransformer,
                                                internalTermApplyInfixTransformer: => InternalTermApplyInfixTransformer,
                                                internalTermApplyTransformer: => InternalTermApplyTransformer,
                                                internalTermSelectTransformer: => InternalTermSelectTransformer,
                                                termTupleToTermApplyTransformer: => TermTupleToTermApplyTransformer,
                                                functionTypeTransformer: => FunctionTypeTransformer,
                                                typeSelectTransformer: TypeSelectTransformer,
                                                typeTupleToTypeApplyTransformer: => TypeTupleToTypeApplyTransformer) extends TreeTransformer {

  override def transform(tree: Tree): Tree = {
    new InnerTransformer()(tree)
  }

  private class InnerTransformer extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case pkg: Pkg => pkgTransformer.transform(pkg)
        case `import`: Import => `import`
        case template: Template => templateTransformer.transform(template)
        case termApplyInfix: Term.ApplyInfix => internalTermApplyInfixTransformer.transform(termApplyInfix)
        case termApply: Term.Apply => internalTermApplyTransformer.transform(termApply)
        case termSelect: Term.Select => internalTermSelectTransformer.transform(termSelect)
        case termTuple: Term.Tuple => termTupleToTermApplyTransformer.transform(termTuple)
        case typeFunction: Type.Function => functionTypeTransformer.transform(typeFunction)
        case typeSelect: Type.Select => typeSelectTransformer.transform(typeSelect).getOrElse(super.apply(typeSelect))
        case typeTuple: Type.Tuple => typeTupleToTypeApplyTransformer.transform(typeTuple)
        case aTree => super.apply(aTree)
      }
  }
}
