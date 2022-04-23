package com.effiban.scala2java

import scala.meta.Mod
import scala.meta.Mod.{Abstract, Final, Private, Protected, Sealed}

object JavaModifiersResolver {

  private final val ScalaModTypeToJavaModifierName: Map[Class[_ <: Mod], String] = Map(
    classOf[Private] -> "private",
    classOf[Protected] -> "protected",
    classOf[Abstract] -> "abstract",
    classOf[Final] -> "final",
    classOf[Sealed] -> "sealed"
  )

  private final val JavaModifierNamePosition = Map(
    "private" -> 0,
    "protected" -> 0,
    "public" -> 0,
    "default" -> 0,
    "static" -> 1,
    "sealed" -> 2,
    "abstract" -> 3,
    "final" -> 4
  )

  def resolveForClass(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Sealed], classOf[Final]))
    modifierNamesBuilder.result()
  }

  def resolveForInterface(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    modifierNamesBuilder += "public"
    modifierNamesBuilder ++= resolve(mods, List(classOf[Sealed]))
    modifierNamesBuilder.result()
  }

  def resolveForClassMethod(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Final]))
    modifierNamesBuilder.result()
  }

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && hasBody) {
      modifierNamesBuilder += "default"
    }
    modifierNamesBuilder ++= resolve(mods, List(classOf[Private]))
    modifierNamesBuilder.result()
  }

  def resolveForClassDataMember(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods, List(classOf[Private], classOf[Protected], classOf[Final]))
    modifierNamesBuilder.result()
  }

  def resolve(inputMods: List[Mod], allowedMods: List[Class[_ <: Mod]]): List[String] = {
    ScalaModTypeToJavaModifierName.filter { case (mod, _) => inputMods.exists(inputMod => mod.isAssignableFrom(inputMod.getClass)) }
      .filter { case (mod, _) => allowedMods.contains(mod) }
      .map { case (_, modifierName) => modifierName }
      .toList
      .sortBy(modifierName => JavaModifierNamePosition.getOrElse(modifierName, Int.MaxValue))
  }
}
