package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

private[unqualifiers] object Unqualifiers {

  private lazy val treeUnqualifier: TreeUnqualifier = new TreeUnqualifierImpl(
    TermSelectUnqualifier,
    TypeSelectUnqualifier,
    TypeProjectUnqualifier
  )


  private[unqualifiers] lazy val pkgUnqualifier: PkgUnqualifier = new PkgUnqualifierImpl(
    StatsByImportSplitter,
    treeUnqualifier
  )

}
