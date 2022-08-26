package effiban.scala2java.resolvers

import effiban.scala2java.orderings.JavaModifierOrdering
import effiban.scala2java.transformers.ScalaToJavaModifierTransformer

import scala.meta.Mod
import scala.meta.Mod.{Private, Protected}

trait JavaModifiersResolver {

  def resolveForClass(mods: List[Mod]): List[String]

  def resolveForInterface(mods: List[Mod]): List[String]

  def resolveForClassMethod(mods: List[Mod]): List[String]

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[String]

  def resolveForClassDataMember(mods: List[Mod]): List[String]

  def resolve(inputScalaMods: List[Mod], allowedJavaModifiers: List[String]): List[String]
}

object JavaModifiersResolver extends JavaModifiersResolver {

  override def resolveForClass(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods,
      List("private", "protected", "abstract", "sealed", "final"))
    modifierNamesBuilder.result()
  }

  override def resolveForInterface(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    modifierNamesBuilder += "public"
    modifierNamesBuilder ++= resolve(mods, List("sealed"))
    modifierNamesBuilder.result()
  }

  override def resolveForClassMethod(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods, List("private", "protected", "abstract", "final"))
    modifierNamesBuilder.result()
  }

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && hasBody) {
      modifierNamesBuilder += "default"
    }
    modifierNamesBuilder ++= resolve(mods, List("private"))
    modifierNamesBuilder.result()
  }

  override def resolveForClassDataMember(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods, List("private", "protected", "final"))
    modifierNamesBuilder.result()
  }

  override def resolve(inputScalaMods: List[Mod], allowedJavaModifiers: List[String]): List[String] = {
    inputScalaMods
      .map(ScalaToJavaModifierTransformer.transform)
      .collect { case Some(javaModifier) => javaModifier }
      .distinct
      .filter(allowedJavaModifiers.contains)
      .sorted(JavaModifierOrdering)
  }
}
