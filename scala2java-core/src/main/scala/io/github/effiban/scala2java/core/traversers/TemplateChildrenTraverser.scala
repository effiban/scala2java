package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.traversers.results.{MultiStatTraversalResult, PopulatedStatTraversalResult}

import scala.meta.Tree

trait TemplateChildrenTraverser {

  def traverse(children: List[Tree], childContext: TemplateChildContext): MultiStatTraversalResult
}

private[traversers] class TemplateChildrenTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                        javaTemplateChildOrdering: JavaTemplateChildOrdering)
  extends TemplateChildrenTraverser {

  def traverse(children: List[Tree], childContext: TemplateChildContext): MultiStatTraversalResult = {
    val statResults = children.sorted(javaTemplateChildOrdering)
      .map(child => templateChildTraverser.traverse(child, childContext))
      .collect { case result: PopulatedStatTraversalResult => result }
    MultiStatTraversalResult(statResults)
  }
}
