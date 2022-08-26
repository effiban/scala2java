package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.orderings.JavaModifierOrdering
import effiban.scala2java.transformers.ScalaToJavaModifierTransformer

import scala.meta.Mod

trait JavaModifiersResolver {

  def resolveForClass(mods: List[Mod]): List[JavaModifier]

  def resolveForInterface(mods: List[Mod]): List[JavaModifier]

  def resolveForClassMethod(mods: List[Mod]): List[JavaModifier]

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[JavaModifier]

  def resolveForClassDataMember(mods: List[Mod]): List[JavaModifier]
}

object JavaModifiersResolver extends JavaModifiersResolver {

  override def resolveForClass(mods: List[Mod]): List[JavaModifier] = {
    resolve(mods, JavaTreeType.Class, javaScope)
  }

  override def resolveForInterface(mods: List[Mod]): List[JavaModifier] = {
    resolve(mods, JavaTreeType.Interface, javaScope)
  }

  override def resolveForClassMethod(mods: List[Mod]): List[JavaModifier] = {
    resolve(mods, JavaTreeType.Method, javaScope)
  }

  override def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[JavaModifier] = {
    resolve(mods, JavaTreeType.Method, javaScope, hasBody)
  }

  override def resolveForClassDataMember(mods: List[Mod]): List[JavaModifier] = {
    resolve(mods, JavaTreeType.DataMember, javaScope)
  }

  def resolve(mods: List[Mod], javaTreeType: JavaTreeType, javaScope: JavaTreeType, hasBody: Boolean = false): List[JavaModifier] = {
    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    val allowedJavaModifiers = JavaAllowedModifiersResolver.resolve(javaTreeType, javaScope)
    modifierNamesBuilder ++= transform(mods, allowedJavaModifiers)

    if (scalaModifiersImplyPublic(mods)) {
      modifierNamesBuilder ++= JavaModifierImplyingPublicResolver.resolve(javaTreeType, javaScope, hasBody)
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
