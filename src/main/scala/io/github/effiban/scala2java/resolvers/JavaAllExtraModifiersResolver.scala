package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.JavaModifier

trait JavaAllExtraModifiersResolver {

  def resolve(context: JavaModifiersContext): Set[JavaModifier]
}

private[resolvers] class JavaAllExtraModifiersResolverImpl(resolvers: Iterable[_ <: JavaExtraModifierResolver]) extends JavaAllExtraModifiersResolver {

  override def resolve(context: JavaModifiersContext): Set[JavaModifier] = {
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