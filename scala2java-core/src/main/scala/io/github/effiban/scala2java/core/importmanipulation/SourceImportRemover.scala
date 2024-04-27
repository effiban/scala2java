package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Pkg, Source}

trait SourceImportRemover {
  def removeUnusedFrom(source: Source): Source

  def removeJavaLangFrom(source: Source): Source
}

private[importmanipulation] class SourceImportRemoverImpl(pkgImportRemover: PkgImportRemover) extends SourceImportRemover {

  override def removeUnusedFrom(source: Source): Source = {
    source.transform {
      case pkg: Pkg => pkgImportRemover.removeUnusedFrom(pkg)
    }.asInstanceOf[Source]
  }

  override def removeJavaLangFrom(source: Source): Source = {
    source.transform {
      case pkg: Pkg => pkgImportRemover.removeJavaLangFrom(pkg)
    }.asInstanceOf[Source]
  }
}

object SourceImportRemover extends SourceImportRemoverImpl(PkgImportRemover)
