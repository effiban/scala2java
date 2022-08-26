package effiban.scala2java.resolvers

import effiban.scala2java.orderings.JavaModifierOrdering

import scala.meta.Mod
import scala.meta.Mod.{Abstract, Final, Private, Protected, Sealed}

trait JavaModifiersResolver {

  def resolveForClass(mods: List[Mod]): List[String]

  def resolveForInterface(mods: List[Mod]): List[String]

  def resolveForClassMethod(mods: List[Mod]): List[String]

  def resolveForInterfaceMethod(mods: List[Mod], hasBody: Boolean): List[String]

  def resolveForClassDataMember(mods: List[Mod]): List[String]

  def resolve(inputMods: List[Mod], allowedMods: List[Class[_ <: Mod]]): List[String]
}

object JavaModifiersResolver extends JavaModifiersResolver {

  private final val ScalaModTypeToJavaModifierName: Map[Class[_ <: Mod], String] = Map(
    classOf[Private] -> "private",
    classOf[Protected] -> "protected",
    classOf[Abstract] -> "abstract",
    classOf[Final] -> "final",
    classOf[Sealed] -> "sealed"
  )

  override def resolveForClass(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Sealed], classOf[Final]))
    modifierNamesBuilder.result()
  }

  override def resolveForInterface(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    modifierNamesBuilder += "public"
    modifierNamesBuilder ++= resolve(mods, List(classOf[Sealed]))
    modifierNamesBuilder.result()
  }

  override def resolveForClassMethod(mods: List[Mod]): List[String] = {
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

  override def resolveForClassDataMember(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolve(mods, List(classOf[Private], classOf[Protected], classOf[Final]))
    modifierNamesBuilder.result()
  }

  override def resolve(inputMods: List[Mod], allowedMods: List[Class[_ <: Mod]]): List[String] = {
    ScalaModTypeToJavaModifierName.filter { case (mod, _) => inputMods.exists(inputMod => mod.isAssignableFrom(inputMod.getClass)) }
      .filter { case (mod, _) => allowedMods.contains(mod) }
      .map { case (_, modifierName) => modifierName }
      .toList
      .sorted(JavaModifierOrdering)
  }
}
