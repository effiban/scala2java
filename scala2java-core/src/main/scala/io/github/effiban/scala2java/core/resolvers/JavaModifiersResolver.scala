package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.orderings.JavaModifierOrdering
import io.github.effiban.scala2java.core.transformers.ModifierTransformer

import scala.meta.Mod

trait JavaModifiersResolver {

  def resolve(context: ModifiersContext): List[JavaModifier]
}

class JavaModifiersResolverImpl(javaAllowedModifiersResolver: JavaAllowedModifiersResolver,
                                javaExtraModifiersResolver: JavaAllExtraModifiersResolver) extends JavaModifiersResolver {

  override def resolve(context: ModifiersContext): List[JavaModifier] = {
    import context._

    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    // Transform the Scala modifiers into corresponding Java modifiers, filtering allowed
    val allowedJavaModifiers = javaAllowedModifiersResolver.resolve(javaTreeType, javaScope)
    modifierNamesBuilder ++= transform(scalaMods, allowedJavaModifiers)

    // Add extra Java-specific modifiers which are required by the context
    modifierNamesBuilder ++= javaExtraModifiersResolver.resolve(context)

    modifierNamesBuilder.result()
      .toList
      .sorted(JavaModifierOrdering)
  }

  private def transform(inputScalaMods: List[Mod], allowedJavaModifiers: Set[JavaModifier]): List[JavaModifier] = {
    inputScalaMods
      .map(ModifierTransformer.transform)
      .collect { case Some(javaModifier) => javaModifier }
      .distinct
      .filter(allowedJavaModifiers.contains)
  }
}

object JavaModifiersResolver extends JavaModifiersResolverImpl(
  JavaAllowedModifiersResolver,
  JavaAllExtraModifiersResolver
)
