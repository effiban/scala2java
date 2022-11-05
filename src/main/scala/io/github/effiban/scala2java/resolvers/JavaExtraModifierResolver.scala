package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.ModifiersContext
import io.github.effiban.scala2java.entities.JavaModifier

trait JavaExtraModifierResolver {

  def resolve(context: ModifiersContext): Option[JavaModifier]
}