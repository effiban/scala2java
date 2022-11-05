package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier

trait JavaExtraModifierResolver {

  def resolve(context: ModifiersContext): Option[JavaModifier]
}