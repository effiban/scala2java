package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.ImportFlattener

import scala.meta.{Import, Pkg, Type}

trait PkgUnqualifier {
  def unqualify(pkg: Pkg): Pkg
}

private[unqualifiers] class PkgUnqualifierImpl(importFlattener: ImportFlattener,
                                               typeSelectUnqualifier: TypeSelectUnqualifier) extends PkgUnqualifier {

  override def unqualify(pkg: Pkg): Pkg = {
    val imports = pkg.stats.collect { case `import`: Import => `import` }
    val importers = importFlattener.flatten(imports)

    pkg.transform {
      case typeSelect: Type.Select => typeSelectUnqualifier.unqualify(typeSelect, importers)
        // TODO support Type.Project, Term.Select
    } match {
      case transformedPkg: Pkg => transformedPkg
      case other => throw new IllegalStateException(s"The transformed Pkg should also be a Pkg but it is: $other")
    }
  }
}

object PkgUnqualifier extends PkgUnqualifierImpl(ImportFlattener, TypeSelectUnqualifier)
