package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{DefnVarClassifier, JavaStatClassifier, TraitClassifier}
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, DefRenderContext}
import io.github.effiban.scala2java.core.renderers.{CtorSecondaryRenderer, DefnDefRenderer, EnumConstantListRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Ctor, Defn, Stat, Tree, Type}

trait TemplateChildTraverser {
  def traverse(child: Tree, context: TemplateChildContext): Unit
}

private[traversers] class TemplateChildTraverserImpl(ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                     defnDefRenderer: => DefnDefRenderer,
                                                     ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                     ctorSecondaryRenderer: => CtorSecondaryRenderer,
                                                     enumConstantListRenderer: => EnumConstantListRenderer,
                                                     statTraverser: => StatTraverser,
                                                     defnVarClassifier: DefnVarClassifier,
                                                     traitClassifier: TraitClassifier,
                                                     javaStatClassifier: JavaStatClassifier)
                                                    (implicit javaWriter: JavaWriter) extends TemplateChildTraverser {

  import javaWriter._

  override def traverse(child: Tree, context: TemplateChildContext): Unit = child match {
    case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, context)
    case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, context)
    case defnVar: Defn.Var if defnVarClassifier.isEnumConstantList(defnVar, context.javaScope) =>
      enumConstantListRenderer.render(defnVar)
      writeStatementEnd()
    // The type definition in a Scala Enumeration is redundant in Java - skip it
    case defnTrait: Defn.Trait if traitClassifier.isEnumTypeDef(defnTrait, context.javaScope) =>
    case stat: Stat => traverseRegularStat(stat, context)
    case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
  }

  private def traversePrimaryCtor(primaryCtor: Ctor.Primary, context: TemplateChildContext): Unit = {
    context.maybeClassName match {
      case Some(className) =>
        val traversalResult = ctorPrimaryTraverser.traverse(primaryCtor, toCtorContext(context, className))
        val renderContext = DefRenderContext(traversalResult.javaModifiers)
        defnDefRenderer.render(traversalResult.tree, renderContext)
      case None => throw new IllegalStateException("Primary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseSecondaryCtor(secondaryCtor: Ctor.Secondary, context: TemplateChildContext): Unit = {
    context.maybeClassName match {
      case Some(className) =>
        val traversalResult = ctorSecondaryTraverser.traverse(secondaryCtor, toCtorContext(context, className))
        val renderContext = CtorSecondaryRenderContext(traversalResult.className, traversalResult.javaModifiers)
        ctorSecondaryRenderer.render(traversalResult.tree, renderContext)
      case None => throw new IllegalStateException("Secondary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseRegularStat(stat: Stat, context: TemplateChildContext): Unit = {
    statTraverser.traverse(stat, StatContext(context.javaScope))
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
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
