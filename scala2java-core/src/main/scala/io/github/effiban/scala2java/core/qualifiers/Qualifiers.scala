package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

private[qualifiers] object Qualifiers {

  private lazy val treeQualifier: TreeQualifier = new TreeQualifierImpl(
    CompositeTermNameQualifier,
    CompositeTypeNameQualifier,
    templateQualifier
  )

  private lazy val templateQualifier: TemplateQualifier = new TemplateQualifierImpl(treeQualifier)

  private[qualifiers] lazy val pkgQualifier: PkgQualifier = new PkgQualifierImpl(StatsByImportSplitter, treeQualifier)
}
