package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.ImporterCollector

import scala.meta.{Importer, Pkg, Transformer, Tree, Type}

trait PkgQualifier {
  def qualify(pkg: Pkg): Pkg
}

private[qualifiers] class PkgQualifierImpl(importerCollector: ImporterCollector,
                                           typeNameQualifier: CompositeTypeNameQualifier) extends PkgQualifier {
  override def qualify(pkg: Pkg): Pkg = {
    val importers = importerCollector.collectFlat(pkg.stats)
    new QualifyingTransformer(importers)(pkg) match {
      case transformedPkg: Pkg => transformedPkg
      case other => throw new IllegalStateException(s"The transformed Pkg should also be a Pkg but it is: $other")
    }
  }

  private class QualifyingTransformer(importers: List[Importer]) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case typeSelect: Type.Select => typeSelect // TODO
        case typeName: Type.Name => typeNameQualifier.qualify(typeName, importers)
        case aTree => super.apply(aTree)
      }
  }
}

object PkgQualifier extends PkgQualifierImpl(ImporterCollector, CompositeTypeNameQualifier)