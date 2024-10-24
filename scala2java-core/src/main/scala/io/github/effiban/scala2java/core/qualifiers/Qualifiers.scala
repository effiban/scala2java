package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

private[qualifiers] object Qualifiers {

  private[qualifiers] lazy val pkgQualifier: PkgQualifier = new PkgQualifierImpl(StatsByImportSplitter, treeQualifier)

  private lazy val templateQualifier: TemplateQualifier = new TemplateQualifierImpl(treeQualifier)

  private lazy val termApplyInfixQualifier: TermApplyInfixQualifier = new TermApplyInfixQualifierImpl(treeQualifier)

  private lazy val treeQualifier: TreeQualifier = new TreeQualifierImpl(
    termApplyInfixQualifier,
    SuperSelectQualifier,
    CompositeTermNameQualifier,
    CompositeTypeNameQualifier,
    templateQualifier
  )

}
