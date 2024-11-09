package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.importmanipulation.PkgImportRemover
import io.github.effiban.scala2java.core.predicates.{CompositeTemplateInitExcludedPredicate, CoreTemplateInitExcludedPredicate}

class Cleanups(implicit extensionRegistry: ExtensionRegistry) {

  val sourceCleanup: SourceCleanup = new SourceCleanupImpl(treeCleanup)

  private lazy val treeCleanup: TreeCleanup = new TreeCleanupImpl(PkgImportRemover)

  val sourceInitCleanup: SourceInitCleanup = new SourceInitCleanupImpl(treeInitCleanup)

  private lazy val templateInitCleanup = new TemplateInitCleanupImpl(
    new CompositeTemplateInitExcludedPredicate(CoreTemplateInitExcludedPredicate)
  )

  private lazy val treeInitCleanup: TreeInitCleanup = new TreeInitCleanupImpl(templateInitCleanup)
}
