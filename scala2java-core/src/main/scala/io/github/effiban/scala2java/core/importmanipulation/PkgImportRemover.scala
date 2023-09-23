package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.Pkg

trait PkgImportRemover {
  def removeFrom(pkg: Pkg): Pkg
}

private[importmanipulation] class PkgImportRemoverImpl extends PkgImportRemover {

  override def removeFrom(pkg: Pkg): Pkg = {
    // TODO
    pkg
  }
}

object PkgImportRemover extends PkgImportRemoverImpl
