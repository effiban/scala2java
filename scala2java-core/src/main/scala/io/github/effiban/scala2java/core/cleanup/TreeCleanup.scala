package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.importmanipulation.PkgImportRemover

import scala.meta.{Pkg, Template, Tree}

trait TreeCleanup {
  def cleanup(tree: Tree): Tree
}

private[cleanup] class TreeCleanupImpl(pkgImportRemover: PkgImportRemover,
                                       templateCleanup: TemplateCleanup) extends TreeCleanup {

  override def cleanup(tree: Tree): Tree = tree.transform {
    case pkg: Pkg => pkgImportRemover.removeJavaLangFrom(pkg)
    case template: Template => templateCleanup.cleanup(template)
  }
}
