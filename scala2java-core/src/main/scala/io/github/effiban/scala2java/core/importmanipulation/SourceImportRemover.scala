package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Pkg, Source}

trait SourceImportRemover {
  def removeFrom(source: Source): Source
}

private[importmanipulation] class SourceImportRemoverImpl(pkgImportRemover: PkgImportRemover) extends SourceImportRemover {

  override def removeFrom(source: Source): Source = {
    source.transform {
      case pkg: Pkg => pkgImportRemover.removeFrom(pkg)
    } match {
      case transformedSource: Source => transformedSource
      case other => throw new IllegalStateException(s"The transformed source should also be a Source but it is: $other")
    }
  }
}

object SourceImportRemover extends SourceImportRemoverImpl(PkgImportRemover)
