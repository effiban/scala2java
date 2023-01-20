package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateBodyContext
import io.github.effiban.scala2java.core.factories.TemplateChildContextFactory
import io.github.effiban.scala2java.core.resolvers.TemplateChildrenResolver
import io.github.effiban.scala2java.core.transformers.TemplateStatTransformer

import scala.meta.{Stat, Term}

trait TemplateBodyTraverser {

  def traverse(statements: List[Stat], context: TemplateBodyContext): Unit
}

private[traversers] class TemplateBodyTraverserImpl(templateChildrenTraverser: => TemplateChildrenTraverser,
                                                    templateStatTransformer: TemplateStatTransformer,
                                                    templateChildrenResolver: TemplateChildrenResolver,
                                                    templateChildContextFactory: TemplateChildContextFactory)
  extends TemplateBodyTraverser {

  def traverse(stats: List[Stat], context: TemplateBodyContext): Unit = {
    val transformedStats = stats.map(templateStatTransformer.transform)
    val (terms, nonTerms) = splitStats(transformedStats)
    val children = templateChildrenResolver.resolve(terms, nonTerms, context)
    val childContext = templateChildContextFactory.create(context, terms)
    templateChildrenTraverser.traverse(children, childContext)
  }

  private def splitStats(stats: List[Stat]) = {
    val terms = stats.collect { case term: Term => term }
    val nonTerms = stats.filterNot(terms.contains(_))
    (terms, nonTerms)
  }
}
