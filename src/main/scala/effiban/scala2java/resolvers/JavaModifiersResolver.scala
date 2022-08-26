package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.orderings.JavaModifierOrdering
import effiban.scala2java.transformers.ScalaToJavaModifierTransformer

import scala.meta.Mod
import scala.meta.Mod.{Private, Protected}

trait JavaModifiersResolver {

  def resolveForClass(mods: List[Mod]): List[JavaModifier]

  def resolveForInterface(mods: List[Mod]): List[JavaModifier]

  def resolveForClassMethod(mods: List[Mod]): List[JavaModifier]

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[JavaModifier]

  def resolveForClassDataMember(mods: List[Mod]): List[JavaModifier]
}

object JavaModifiersResolver extends JavaModifiersResolver {

  override def resolveForClass(mods: List[Mod]): List[JavaModifier] = {
    val modifierNamesBuilder = List.newBuilder[JavaModifier]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += JavaModifier.Public
    }
    modifierNamesBuilder ++= resolve(mods,
      List(JavaModifier.Private, JavaModifier.Protected, JavaModifier.Abstract, JavaModifier.Sealed, JavaModifier.Final))
    modifierNamesBuilder.result()
  }

  override def resolveForInterface(mods: List[Mod]): List[JavaModifier] = {
    val modifierNamesBuilder = List.newBuilder[JavaModifier]
    modifierNamesBuilder += JavaModifier.Public
    modifierNamesBuilder ++= resolve(mods, List(JavaModifier.Sealed))
    modifierNamesBuilder.result()
  }

  override def resolveForClassMethod(mods: List[Mod]): List[JavaModifier] = {
    val modifierNamesBuilder = List.newBuilder[JavaModifier]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += JavaModifier.Public
    }
    modifierNamesBuilder ++= resolve(mods, List(JavaModifier.Private, JavaModifier.Protected, JavaModifier.Abstract, JavaModifier.Final))
    modifierNamesBuilder.result()
  }

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[JavaModifier] = {
    val modifierNamesBuilder = List.newBuilder[JavaModifier]
    if (!mods.exists(_.isInstanceOf[Private]) && hasBody) {
      modifierNamesBuilder += JavaModifier.Default
    }
    modifierNamesBuilder ++= resolve(mods, List(JavaModifier.Private))
    modifierNamesBuilder.result()
  }

  override def resolveForClassDataMember(mods: List[Mod]): List[JavaModifier] = {
    val modifierNamesBuilder = List.newBuilder[JavaModifier]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += JavaModifier.Public
    }
    modifierNamesBuilder ++= resolve(mods, List(JavaModifier.Private, JavaModifier.Protected, JavaModifier.Final))
    modifierNamesBuilder.result()
  }

  private def resolve(inputScalaMods: List[Mod], allowedJavaModifiers: List[JavaModifier]): List[JavaModifier] = {
    inputScalaMods
      .map(ScalaToJavaModifierTransformer.transform)
      .collect { case Some(javaModifier) => javaModifier }
      .distinct
      .filter(allowedJavaModifiers.contains)
      .sorted(JavaModifierOrdering)
  }
}
