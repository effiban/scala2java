package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.DefnTypeClassifier
import io.github.effiban.scala2java.core.contexts.TemplateBodyContext

import scala.meta.{Defn, Stat, Term, Tree}

trait TemplateChildrenResolver {
  def resolve(terms: List[Term], nonTerms: List[Stat], context: TemplateBodyContext): List[Tree]
}

private[resolvers] class TemplateChildrenResolverImpl(defnTypeClassifier: DefnTypeClassifier) extends TemplateChildrenResolver {

  override def resolve(terms: List[Term], nonTerms: List[Stat], context: TemplateBodyContext): List[Tree] = {
    // The particular Scala typedef required for an enumeration is redundant in Java
    val filteredNonTerms = nonTerms.filterNot {
      case defnType: Defn.Type => isEnumTypeDef(defnType, context)
      case _ => false
    }

    context.maybePrimaryCtor match {
      case Some(primaryCtor) => filteredNonTerms :+ primaryCtor
      case None => terms ++ filteredNonTerms
    }
  }

  private def isEnumTypeDef(defnType: Defn.Type, context: TemplateBodyContext): Boolean = {
    defnTypeClassifier.isEnumTypeDef(defnType, context.javaScope)
  }
}

object TemplateChildrenResolver extends TemplateChildrenResolverImpl(DefnTypeClassifier)
