package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.DefnTypeClassifier
import io.github.effiban.scala2java.core.contexts.{CtorRequiredResolutionContext, TemplateBodyContext}

import scala.meta.{Defn, Stat, Term, Tree}

trait TemplateChildrenResolver {
  def resolve(terms: List[Term], nonTerms: List[Stat], context: TemplateBodyContext): List[Tree]
}

private[resolvers] class TemplateChildrenResolverImpl(defnTypeClassifier: DefnTypeClassifier,
                                                      javaCtorPrimaryRequiredResolver: JavaCtorPrimaryRequiredResolver) extends TemplateChildrenResolver {

  override def resolve(terms: List[Term], nonTerms: List[Stat], context: TemplateBodyContext): List[Tree] = {
    import javaCtorPrimaryRequiredResolver._

    // The particular Scala typedef required for an enumeration is redundant in Java
    val filteredNonTerms = nonTerms.filterNot {
      case defnType: Defn.Type => isEnumTypeDef(defnType, context)
      case _ => false
    }

    val ctorRequiredResolutionContext = CtorRequiredResolutionContext(
      inits = context.inits,
      terms = terms,
      otherStats = filteredNonTerms
    )

    context.maybePrimaryCtor match {
      case Some(primaryCtor) if isRequired(primaryCtor, ctorRequiredResolutionContext) => filteredNonTerms :+ primaryCtor
      case _ => terms ++ filteredNonTerms
    }
  }

  private def isEnumTypeDef(defnType: Defn.Type, context: TemplateBodyContext): Boolean = {
    defnTypeClassifier.isEnumTypeDef(defnType, context.javaScope)
  }
}

object TemplateChildrenResolver extends TemplateChildrenResolverImpl(DefnTypeClassifier, JavaCtorPrimaryRequiredResolver)
