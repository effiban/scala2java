package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering

import scala.meta.{Stat, Tree}

trait TemplateChildrenTraverser {

  def traverse(children: List[Tree], childContext: TemplateChildContext): List[Stat]
}

private[traversers] class TemplateChildrenTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                        javaTemplateChildOrdering: JavaTemplateChildOrdering)
  extends TemplateChildrenTraverser {

  def traverse(children: List[Tree], childContext: TemplateChildContext): List[Stat] = {
    children.sorted(javaTemplateChildOrdering)
      .map(child => templateChildTraverser.traverse(child, childContext))
      .collect { case Some(traversedChild) => traversedChild }
  }
}
