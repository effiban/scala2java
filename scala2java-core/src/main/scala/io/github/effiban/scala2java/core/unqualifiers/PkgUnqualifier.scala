package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

import scala.meta.{Import, Importer, Pkg, Stat, Term, Transformer, Tree, Type}

trait PkgUnqualifier {
  def unqualify(pkg: Pkg): Pkg
}

private[unqualifiers] class PkgUnqualifierImpl(statsByImportSplitter: StatsByImportSplitter,
                                               termApplyUnqualifier: TermApplyUnqualifier,
                                               typeSelectUnqualifier: TypeSelectUnqualifier) extends PkgUnqualifier {

  override def unqualify(pkg: Pkg): Pkg = {
    val (importers, nonImports) = statsByImportSplitter.split(pkg.stats)
    val imports = importers.map(importer => Import(List(importer)))
    val unqualifiedStats = nonImports.map(stat => unqualify(stat, importers))

    pkg.copy(stats = imports ++ unqualifiedStats)
  }

  private def unqualify(stat: Stat, importers: List[Importer]): Stat = {
    new UnqualifyingTransformer(importers)(stat) match {
      case unqualifiedStat: Stat => unqualifiedStat
      case other => throw new IllegalStateException(s"An unqualified Stat should also be a Stat but it is: $other")
    }
  }

  private class UnqualifyingTransformer(importers: List[Importer]) extends Transformer {

    override def apply(tree: Tree): Tree =
      tree match {
        case termApply: Term.Apply => termApplyUnqualifier.unqualify(termApply, importers)
        case termSelect: Term.Select => termSelect // TODO support standalone qualified terms when relevant using reflection
        case typeSelect: Type.Select => typeSelectUnqualifier.unqualify(typeSelect, importers)
        // TODO support Type.Project
        case aTree => super.apply(aTree)
      }
  }
}

object PkgUnqualifier extends PkgUnqualifierImpl(
  StatsByImportSplitter,
  TermApplyUnqualifier,
  TypeSelectUnqualifier
)
