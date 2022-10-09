package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}

trait JavaAllowedModifiersResolver {

  def resolve(treeType: JavaTreeType, scope: JavaScope): Set[JavaModifier]
}

object JavaAllowedModifiersResolver extends JavaAllowedModifiersResolver {

  private final val OuterClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val InnerClassOfClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val InnerClassOfInterfaceAllowedModifiers = Set[JavaModifier](
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val OuterInterfaceAllowedModifiers = Set[JavaModifier](JavaModifier.Public, JavaModifier.Sealed)

  private final val InnerInterfaceOfClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Sealed
  )

  private final val InnerInterfaceOfInterfaceAllowedModifiers = Set[JavaModifier](JavaModifier.Sealed)

  private final val ClassMethodAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val InterfaceMethodAllowedModifiers = Set[JavaModifier](JavaModifier.Private, JavaModifier.Default)

  private final val ClassVariableAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Final
  )

  private final val LocalVariableAllowedModifiers = Set[JavaModifier](JavaModifier.Final)

  private final val ParameterAllowedModifiers = Set[JavaModifier](JavaModifier.Final)

  override def resolve(treeType: JavaTreeType, scope: JavaScope): Set[JavaModifier] = {

    (treeType, scope) match {
      case (treeTpe, theScope) if isClassLikeTree(treeTpe) && isPackageLikeScope(theScope) => OuterClassAllowedModifiers
      case (treeTpe, theScope) if isClassLikeTree(treeTpe) && isClassLikeScope(theScope) => InnerClassOfClassAllowedModifiers
      case (treeTpe, JavaScope.Interface) if isClassLikeTree(treeTpe) => InnerClassOfInterfaceAllowedModifiers
      case (JavaTreeType.Interface, theScope) if isPackageLikeScope(theScope) => OuterInterfaceAllowedModifiers
      case (JavaTreeType.Interface, theScope) if isClassLikeScope(theScope) => InnerInterfaceOfClassAllowedModifiers
      case (JavaTreeType.Interface, JavaScope.Interface) => InnerInterfaceOfInterfaceAllowedModifiers
      case (JavaTreeType.Method, theScope) if isClassLikeScope(theScope) => ClassMethodAllowedModifiers
      case (JavaTreeType.Method, JavaScope.Interface) => InterfaceMethodAllowedModifiers
      case (JavaTreeType.Variable, theScope) if isClassLikeScope(theScope) => ClassVariableAllowedModifiers
      case (JavaTreeType.Variable, JavaScope.Block) => LocalVariableAllowedModifiers
      case (JavaTreeType.Parameter, _) => ParameterAllowedModifiers
      case _ => Set.empty
    }
  }

  private def isPackageLikeScope(scope: JavaScope) = scope match {
    case JavaScope.Package | JavaScope.Sealed => true
    case _ => false
  }

  private def isClassLikeTree(treeType: JavaTreeType) = treeType match {
    case JavaTreeType.Class | JavaTreeType.Record | JavaTreeType.Enum => true
    case _ => false
  }

  private def isClassLikeScope(scope: JavaScope) = scope match {
    case JavaScope.Class | JavaScope.UtilityClass | JavaScope.Enum => true
    case _ => false
  }
}
