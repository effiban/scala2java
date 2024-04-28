package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Import, Pkg, Stat, Term, Transformer, Tree, Type}

trait StatQualifier {
  def qualify(stat: Stat, context: QualificationContext = QualificationContext()): Stat
}

private[qualifiers] class StatQualifierImpl(termNameQualifier: CompositeTermNameQualifier,
                                            typeNameQualifier: CompositeTypeNameQualifier) extends StatQualifier {
  override def qualify(stat: Stat, context: QualificationContext = QualificationContext()): Stat = {
    new QualifyingTransformer(context)(stat).asInstanceOf[Stat]
  }

  private class QualifyingTransformer(context: QualificationContext) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case termSelect: Term.Select => termSelect.copy(qual = apply(termSelect.qual).asInstanceOf[Term])
        case termName: Term.Name => termNameQualifier.qualify(termName, context)
        case typeSelect: Type.Select => typeSelect.copy(qual = apply(typeSelect.qual).asInstanceOf[Term.Ref])
        case typeProject: Type.Project => typeProject.copy(qual = apply(typeProject.qual).asInstanceOf[Type])
        case typeName: Type.Name => typeNameQualifier.qualify(typeName, context)
        case `import`: Import => `import`// TODO
        case pkg: Pkg => pkg // TODO
        case aTree => super.apply(aTree)
      }
  }
}

object StatQualifier extends StatQualifierImpl(
  CompositeTermNameQualifier,
  CompositeTypeNameQualifier
)