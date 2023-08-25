package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Pkg, Transformer, Tree, Type}

trait PkgQualifier {
  def qualify(pkg: Pkg): Pkg
}

private[qualifiers] class PkgQualifierImpl(coreTypeNameQualifier: CoreTypeNameQualifier) extends PkgQualifier {
  override def qualify(pkg: Pkg): Pkg =
    QualifyingTransformer(pkg) match {
      case transformedPkg: Pkg => transformedPkg
      case other => throw new IllegalStateException(s"The transformed Pkg should also be a Pkg but it is: $other")
    }

  private object QualifyingTransformer extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case typeSelect: Type.Select => typeSelect // TODO
        case typeName: Type.Name => coreTypeNameQualifier.qualify(typeName).getOrElse(typeName)
        case aTree => super.apply(aTree)
      }
  }
}

object PkgQualifier extends PkgQualifierImpl(CoreTypeNameQualifier)