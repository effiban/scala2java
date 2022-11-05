package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.ModifiersContext
import io.github.effiban.scala2java.entities.JavaModifier

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
