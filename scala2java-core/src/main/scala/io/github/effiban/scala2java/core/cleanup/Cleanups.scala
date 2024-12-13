package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector

object Cleanups {

  val sourceInitCleanup: SourceInitCleanup = new SourceInitCleanupImpl(treeInitCleanup)

  private val templateDirectParentsUsedResolver = new TemplateParentsUsedResolverImpl(
    TemplateAncestorsCollector,
    CompositeIsTemplateAncestorUsed
  )

  private val templateInitCleanup = new TemplateInitCleanupImpl(templateDirectParentsUsedResolver)

  private lazy val treeInitCleanup: TreeInitCleanup = new TreeInitCleanupImpl(templateInitCleanup)
}
