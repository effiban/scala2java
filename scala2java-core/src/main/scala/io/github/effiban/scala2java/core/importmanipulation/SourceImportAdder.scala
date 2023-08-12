package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Pkg, Source}

trait SourceImportAdder {
  def addTo(source: Source): Source
}

private[importmanipulation] class SourceImportAdderImpl(pkgImportAdder: PkgImportAdder) extends SourceImportAdder {

  override def addTo(source: Source): Source = {
    source.transform {
      case pkg: Pkg => pkgImportAdder.addTo(pkg)
    } match {
      case transformedSource: Source => transformedSource
      case other => throw new IllegalStateException(s"The transformed source should also be a Source but it is: $other")
    }
  }
}

object SourceImportAdder extends SourceImportAdderImpl(PkgImportAdder)
