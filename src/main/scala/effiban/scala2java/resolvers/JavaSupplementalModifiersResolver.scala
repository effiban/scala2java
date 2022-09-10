package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.predicates.JavaRequiresFinalModifierPredicate

import scala.meta.Mod

trait JavaSupplementalModifiersResolver {

  def resolve(params: JavaModifiersResolverParams): Set[JavaModifier]
}

class JavaSupplementalModifiersResolverImpl(javaPublicModifierResolver: JavaPublicModifierResolver,
                                            javaRequiresFinalModifierPredicate: JavaRequiresFinalModifierPredicate)
  extends JavaSupplementalModifiersResolver {

  override def resolve(params: JavaModifiersResolverParams): Set[JavaModifier] = {
    import params._

    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    if (scalaModifiersImplyPublic(scalaMods)) {
      modifierNamesBuilder ++= javaPublicModifierResolver.resolve(params)
    }

    if (javaRequiresFinalModifierPredicate.apply(params)) {
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
