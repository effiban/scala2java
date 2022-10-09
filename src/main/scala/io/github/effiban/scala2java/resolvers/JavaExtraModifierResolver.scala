package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.JavaModifier

trait JavaExtraModifierResolver {

  def resolve(context: JavaModifiersContext): Option[JavaModifier]
}