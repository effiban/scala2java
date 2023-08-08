package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.TraitClassifier
import io.github.effiban.scala2java.core.contexts._

import scala.meta.{Ctor, Defn, Stat, Tree, Type}

trait TemplateChildTraverser {
  def traverse(child: Tree, context: TemplateChildContext): Option[Stat]
}

private[traversers] class TemplateChildTraverserImpl(ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                     ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                     defaultStatTraverser: => DefaultStatTraverser,
                                                     traitClassifier: TraitClassifier) extends TemplateChildTraverser {

  override def traverse(child: Tree, context: TemplateChildContext): Option[Stat] = child match {
    case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, context)
    case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, context)
    // The type definition in a Scala Enumeration is redundant in Java - skip it
    case defnTrait: Defn.Trait if traitClassifier.isEnumTypeDef(defnTrait, context.javaScope) => None
    case stat: Stat => defaultStatTraverser.traverse(stat, StatContext(context.javaScope))
    case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
  }

  private def traversePrimaryCtor(primaryCtor: Ctor.Primary, context: TemplateChildContext) = {
    context.maybeClassName match {
      case Some(className) => Some(ctorPrimaryTraverser.traverse(primaryCtor, toCtorContext(context, className)).tree)
      case None => throw new IllegalStateException("Primary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseSecondaryCtor(secondaryCtor: Ctor.Secondary, context: TemplateChildContext) = {
    context.maybeClassName match {
      case Some(className) => Some(ctorSecondaryTraverser.traverse(secondaryCtor, toCtorContext(context, className)).tree)
      case None => throw new IllegalStateException("Secondary Ctor. exists but no context could be constructed for it")
    }
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
