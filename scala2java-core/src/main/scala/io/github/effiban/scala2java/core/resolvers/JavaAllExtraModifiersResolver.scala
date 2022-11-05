package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier

trait JavaAllExtraModifiersResolver {

  def resolve(context: ModifiersContext): Set[JavaModifier]
}

private[resolvers] class JavaAllExtraModifiersResolverImpl(resolvers: Iterable[_ <: JavaExtraModifierResolver]) extends JavaAllExtraModifiersResolver {

  override def resolve(context: ModifiersContext): Set[JavaModifier] = {
    resolvers.map(_.resolve(context))
      .collect { case Some(javaMod) => javaMod }
      .toSet
  }
}

object JavaAllExtraModifiersResolver extends JavaAllExtraModifiersResolverImpl(
  List(
    JavaPublicModifierResolver,
    JavaStaticModifierResolver,
    JavaNonSealedModifierResolver,
    JavaFinalModifierResolver
  )
)
