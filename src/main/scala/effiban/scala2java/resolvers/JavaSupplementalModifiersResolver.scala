package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.predicates.JavaRequiresFinalModifierPredicate

import scala.meta.Mod

trait JavaSupplementalModifiersResolver {

  def resolve(context: JavaModifiersContext): Set[JavaModifier]
}

class JavaSupplementalModifiersResolverImpl(javaPublicModifierResolver: JavaPublicModifierResolver,
                                            javaRequiresFinalModifierPredicate: JavaRequiresFinalModifierPredicate)
  extends JavaSupplementalModifiersResolver {

  override def resolve(context: JavaModifiersContext): Set[JavaModifier] = {
    import context._

    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    if (scalaModifiersImplyPublic(scalaMods)) {
      modifierNamesBuilder ++= javaPublicModifierResolver.resolve(context)
    }

    if (javaRequiresFinalModifierPredicate.apply(context)) {
      modifierNamesBuilder += JavaModifier.Final
    }

    modifierNamesBuilder.result()
  }

  private def scalaModifiersImplyPublic(mods: List[Mod]) = {
    mods.collect {
      case m: Mod.Private => m
      case m: Mod.Protected => m
    }.isEmpty
  }
}

object JavaSupplementalModifiersResolver extends JavaSupplementalModifiersResolverImpl(
  JavaPublicModifierResolver,
  JavaRequiresFinalModifierPredicate
)
