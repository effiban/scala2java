package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.importmanipulation.PkgImportRemover

import scala.meta.{Pkg, Tree}

trait TreeCleanup {
  def cleanup(tree: Tree): Tree
}

private[cleanup] class TreeCleanupImpl(pkgImportRemover: PkgImportRemover) extends TreeCleanup {

  override def cleanup(tree: Tree): Tree = tree.transform {
    case pkg: Pkg => pkgImportRemover.removeJavaLangFrom(pkg)
  }
}
