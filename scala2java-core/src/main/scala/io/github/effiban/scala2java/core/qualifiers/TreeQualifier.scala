package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Import, Pkg, Template, Term, Transformer, Tree, Type}

trait TreeQualifier {
  def qualify(tree: Tree, context: QualificationContext = QualificationContext()): Tree
}

private[qualifiers] class TreeQualifierImpl(termNameQualifier: CompositeTermNameQualifier,
                                            typeNameQualifier: CompositeTypeNameQualifier,
                                            templateQualifier: => TemplateQualifier) extends TreeQualifier {
  override def qualify(tree: Tree, context: QualificationContext = QualificationContext()): Tree = {
    new QualifyingTransformer(context)(tree)
  }

  private class QualifyingTransformer(context: QualificationContext) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case termSelect: Term.Select => termSelect.copy(qual = apply(termSelect.qual).asInstanceOf[Term])
        case termName: Term.Name => termNameQualifier.qualify(termName, context)
        case typeSelect: Type.Select => typeSelect.copy(qual = apply(typeSelect.qual).asInstanceOf[Term.Ref])
        case typeProject: Type.Project => typeProject.copy(qual = apply(typeProject.qual).asInstanceOf[Type])
        case typeName: Type.Name => typeNameQualifier.qualify(typeName, context)
        case template: Template => templateQualifier.qualify(template, context)
        case `import`: Import => `import`// TODO
        case pkg: Pkg => pkg // TODO
        case aTree => super.apply(aTree)
      }
  }
}
