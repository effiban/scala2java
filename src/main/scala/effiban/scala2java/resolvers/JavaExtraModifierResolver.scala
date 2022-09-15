package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaModifier

trait JavaExtraModifierResolver {

  def resolve(context: JavaModifiersContext): Option[JavaModifier]
}