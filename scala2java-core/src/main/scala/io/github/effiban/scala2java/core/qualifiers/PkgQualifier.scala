package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

import scala.meta.{Import, Importer, Pkg, Stat, Term, Transformer, Tree, Type}

trait PkgQualifier {
  def qualify(pkg: Pkg): Pkg
}

private[qualifiers] class PkgQualifierImpl(statsByImportSplitter: StatsByImportSplitter,
                                           termNameQualifier: CompositeTermNameQualifier,
                                           typeNameQualifier: CompositeTypeNameQualifier) extends PkgQualifier {
  override def qualify(pkg: Pkg): Pkg = {
    val (importers, nonImports) = statsByImportSplitter.split(pkg.stats)
    val imports = importers.map(importer => Import(List(importer)))
    val qualifiedStats = nonImports.map(stat => qualify(stat, importers))

    pkg.copy(stats = imports ++ qualifiedStats)
  }

  private def qualify(stat: Stat, importers: List[Importer]): Stat = {
    new QualifyingTransformer(importers)(stat) match {
      case qualifiedStat: Stat => qualifiedStat
      case other => throw new IllegalStateException(s"A qualified Stat should also be a Stat but it is: $other")
    }
  }

  private class QualifyingTransformer(importers: List[Importer]) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case termSelect: Term.Select => termSelect.copy(qual = apply(termSelect.qual).asInstanceOf[Term])
        case termName: Term.Name => termNameQualifier.qualify(termName, importers)
        case typeSelect: Type.Select => typeSelect.copy(qual = apply(typeSelect.qual).asInstanceOf[Term.Ref])
        case typeProject: Type.Project => typeProject.copy(qual = apply(typeProject.qual).asInstanceOf[Type])
        case typeName: Type.Name => typeNameQualifier.qualify(typeName, importers)
        case aTree => super.apply(aTree)
      }
  }
}

object PkgQualifier extends PkgQualifierImpl(
  StatsByImportSplitter,
  CompositeTermNameQualifier,
  CompositeTypeNameQualifier
)