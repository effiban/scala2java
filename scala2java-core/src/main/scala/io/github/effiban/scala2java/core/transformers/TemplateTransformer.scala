package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.SameTypeTransformer0

import scala.meta.{Init, Self, Stat, Template}

trait TemplateTransformer extends SameTypeTransformer0[Template]

private[transformers] class TemplateTransformerImpl(treeTransformer: => TreeTransformer) extends TemplateTransformer {

  // This transformer is provided only for determining the order, since the stats must be transformed
  // while their parent is still the original template (and not one with transformed inits/self),
  // in case there is a connection between them such as a reference to a superclass.
  // The default recursion cannot guarantee that
  override def transform(template: Template): Template = {
    val transformedInits = template.inits.map(treeTransformer.transform(_).asInstanceOf[Init])
    val transformedSelf = treeTransformer.transform(template.self).asInstanceOf[Self]
    val transformedStats = template.stats.map(treeTransformer.transform(_).asInstanceOf[Stat])

    Template(
      // TODO - transform early-s
      early = Nil,
      inits = transformedInits,
      self = transformedSelf,
      stats = transformedStats
    )
  }
}
