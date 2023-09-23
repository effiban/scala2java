package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Pkg, Source}

trait SourceImportRemover {
  def removeUnusedFrom(source: Source): Source
}

private[importmanipulation] class SourceImportRemoverImpl(pkgImportRemover: PkgImportRemover) extends SourceImportRemover {

  override def removeUnusedFrom(source: Source): Source = {
    source.transform {
      case pkg: Pkg => pkgImportRemover.removeUnusedFrom(pkg)
    } match {
      case transformedSource: Source => transformedSource
      case other => throw new IllegalStateException(s"The transformed source should also be a Source but it is: $other")
    }
  }
}

object SourceImportRemover extends SourceImportRemoverImpl(PkgImportRemover)
