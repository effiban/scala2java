package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.ImporterCollector

import scala.meta.{Pkg, Type}

trait PkgUnqualifier {
  def unqualify(pkg: Pkg): Pkg
}

private[unqualifiers] class PkgUnqualifierImpl(importerCollector: ImporterCollector,
                                               typeSelectUnqualifier: TypeSelectUnqualifier) extends PkgUnqualifier {

  override def unqualify(pkg: Pkg): Pkg = {
    val importers = importerCollector.collectFlat(pkg.stats)

    pkg.transform {
      case typeSelect: Type.Select => typeSelectUnqualifier.unqualify(typeSelect, importers)
        // TODO support Type.Project, Term.Select
    } match {
      case transformedPkg: Pkg => transformedPkg
      case other => throw new IllegalStateException(s"The transformed Pkg should also be a Pkg but it is: $other")
    }
  }
}

object PkgUnqualifier extends PkgUnqualifierImpl(ImporterCollector, TypeSelectUnqualifier)
