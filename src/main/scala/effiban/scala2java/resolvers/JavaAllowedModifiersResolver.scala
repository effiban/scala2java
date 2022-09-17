package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.{Block, Interface, JavaTreeType, Method, Package, Parameter, Record, Variable}
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}

trait JavaAllowedModifiersResolver {

  def resolve(treeType: JavaTreeType, scope: JavaTreeType): Set[JavaModifier]
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

  override def resolve(treeType: JavaTreeType, scope: JavaTreeType): Set[JavaModifier] = {

    (treeType, scope) match {
      case (treeTpe, Package) if isClassLikeTree(treeTpe) => OuterClassAllowedModifiers
      case (treeTpe, theScope) if isClassLikeTree(treeTpe) && isClassLikeScope(theScope) => InnerClassOfClassAllowedModifiers
      case (treeTpe, Interface) if isClassLikeTree(treeTpe) => InnerClassOfInterfaceAllowedModifiers
      case (Interface, Package) => OuterInterfaceAllowedModifiers
      case (Interface, theScope) if isClassLikeScope(theScope) => InnerInterfaceOfClassAllowedModifiers
      case (Interface, Interface) => InnerInterfaceOfInterfaceAllowedModifiers
      case (Method, theScope) if isClassLikeScope(theScope) => ClassMethodAllowedModifiers
      case (Method, Interface) => InterfaceMethodAllowedModifiers
      case (Variable, theScope) if isClassLikeScope(theScope) => ClassVariableAllowedModifiers
      case (Variable, Block) => LocalVariableAllowedModifiers
      case (Parameter, _) => ParameterAllowedModifiers
      case _ => Set.empty
    }
  }

  private def isClassLikeTree(treeType: JavaTreeType) = treeType match {
    case JavaTreeType.Class | Record | JavaTreeType.Enum => true
    case _ => false
  }

  private def isClassLikeScope(treeType: JavaTreeType) = treeType match {
    case JavaTreeType.Class | JavaTreeType.Enum => true
    case _ => false
  }
}
