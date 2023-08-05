package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{DefnVarClassifier, TraitClassifier}
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.traversers.results.{EmptyStatTraversalResult, EnumConstantListTraversalResult, SimpleStatTraversalResult, StatTraversalResult}

import scala.meta.{Ctor, Defn, Stat, Tree, Type}

trait TemplateChildTraverser {
  def traverse(child: Tree, context: TemplateChildContext): StatTraversalResult
}

private[traversers] class TemplateChildTraverserImpl(ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                     ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                     defaultStatTraverser: => DefaultStatTraverser,
                                                     defnVarClassifier: DefnVarClassifier,
                                                     traitClassifier: TraitClassifier) extends TemplateChildTraverser {

  override def traverse(child: Tree, context: TemplateChildContext): StatTraversalResult = child match {
    case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, context)
    case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, context)
    case defnVar: Defn.Var if defnVarClassifier.isEnumConstantList(defnVar, context.javaScope) => EnumConstantListTraversalResult(defnVar)
    // The type definition in a Scala Enumeration is redundant in Java - skip it
    case defnTrait: Defn.Trait if traitClassifier.isEnumTypeDef(defnTrait, context.javaScope) => EmptyStatTraversalResult
    case stat: Stat => traverseRegularStat(stat, context)
    case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
  }

  private def traversePrimaryCtor(primaryCtor: Ctor.Primary, context: TemplateChildContext) = {
    context.maybeClassName match {
      case Some(className) => ctorPrimaryTraverser.traverse(primaryCtor, toCtorContext(context, className))
      case None => throw new IllegalStateException("Primary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseSecondaryCtor(secondaryCtor: Ctor.Secondary, context: TemplateChildContext) = {
    context.maybeClassName match {
      case Some(className) => ctorSecondaryTraverser.traverse(secondaryCtor, toCtorContext(context, className))
      case None => throw new IllegalStateException("Secondary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseRegularStat(stat: Stat, context: TemplateChildContext) = {
    defaultStatTraverser.traverse(stat, StatContext(context.javaScope))
      .map(SimpleStatTraversalResult(_))
      .getOrElse(EmptyStatTraversalResult)
  }

  private def toCtorContext(childContext: TemplateChildContext, className: Type.Name) = {
    CtorContext(
      javaScope = childContext.javaScope,
      className = className,
      inits = childContext.inits,
      terms = childContext.ctorTerms
    )
  }
}
