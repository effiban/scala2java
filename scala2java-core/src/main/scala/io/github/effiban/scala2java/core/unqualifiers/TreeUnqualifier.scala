package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.meta.{Import, Pkg, Template, Term, Transformer, Tree, Type}

trait TreeUnqualifier {
  def unqualify(tree: Tree, context: QualificationContext = QualificationContext()): Tree
}

private[unqualifiers] class TreeUnqualifierImpl(termSelectUnqualifier: TermSelectUnqualifier,
                                                typeSelectUnqualifier: TypeSelectUnqualifier,
                                                typeProjectUnqualifier: TypeProjectUnqualifier,
                                                templateUnqualifier: => TemplateUnqualifier) extends TreeUnqualifier {

  override def unqualify(tree: Tree, context: QualificationContext = QualificationContext()): Tree = {
    new UnqualifyingTransformer(context)(tree)
  }

  private class UnqualifyingTransformer(context: QualificationContext) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case `import`: Import => `import`// TODO handle imports below top level
        case termSelect: Term.Select => termSelectUnqualifier.unqualify(termSelect, context) match {
          case aTermSelect: Term.Select => super.apply(aTermSelect)
          case aTermRef => aTermRef
        }
        case typeSelect: Type.Select => typeSelectUnqualifier.unqualify(typeSelect, context)
        case typeProject: Type.Project => typeProjectUnqualifier.unqualify(typeProject, context) match {
          case aTypeProject: Type.Project => super.apply(aTypeProject)
          case aTypeRef => aTypeRef
        }
        case template: Template => templateUnqualifier.unqualify(template, context)
        case pkg: Pkg => pkg // TODO handle nested packages
        case aTree => super.apply(aTree)
      }
  }
}
