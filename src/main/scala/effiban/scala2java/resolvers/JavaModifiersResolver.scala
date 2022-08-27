package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.orderings.JavaModifierOrdering
import effiban.scala2java.transformers.ScalaToJavaModifierTransformer

import scala.meta.Mod

trait JavaModifiersResolver {

  def resolve(params: JavaModifiersResolverParams): List[JavaModifier]
}

object JavaModifiersResolver extends JavaModifiersResolver {

  override def resolve(params: JavaModifiersResolverParams): List[JavaModifier] = {
    import params._

    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    val allowedJavaModifiers = JavaAllowedModifiersResolver.resolve(javaTreeType, javaScope)
    modifierNamesBuilder ++= transform(scalaMods, allowedJavaModifiers)

    if (scalaModifiersImplyPublic(scalaMods)) {
      modifierNamesBuilder ++= JavaModifierImplyingPublicResolver.resolve(scalaTree, javaTreeType, javaScope)
    }

    modifierNamesBuilder.result()
      .toList
      .sorted(JavaModifierOrdering)
  }

  private def transform(inputScalaMods: List[Mod], allowedJavaModifiers: Set[JavaModifier]): List[JavaModifier] = {
    inputScalaMods
      .map(ScalaToJavaModifierTransformer.transform)
      .collect { case Some(javaModifier) => javaModifier }
      .distinct
      .filter(allowedJavaModifiers.contains)
  }

  private def scalaModifiersImplyPublic(mods: List[Mod]) = {
    mods.collect {
      case m: Mod.Private => m
      case m: Mod.Protected => m
    }.isEmpty
  }
}
