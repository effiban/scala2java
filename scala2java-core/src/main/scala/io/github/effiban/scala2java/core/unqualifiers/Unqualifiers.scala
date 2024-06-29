package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

private[unqualifiers] object Unqualifiers {

  private lazy val treeUnqualifier: TreeUnqualifier = new TreeUnqualifierImpl(
    TermSelectUnqualifier,
    TypeSelectUnqualifier,
    TypeProjectUnqualifier,
    templateUnqualifier
  )

  private lazy val templateUnqualifier: TemplateUnqualifier = new TemplateUnqualifierImpl(treeUnqualifier)

  private[unqualifiers] lazy val pkgUnqualifier: PkgUnqualifier = new PkgUnqualifierImpl(
    StatsByImportSplitter,
    treeUnqualifier
  )

}
