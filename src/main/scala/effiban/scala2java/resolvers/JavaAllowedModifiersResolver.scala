package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
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
      case (JavaTreeType.Class | JavaTreeType.Record, JavaTreeType.Package) => OuterClassAllowedModifiers
      case (JavaTreeType.Class | JavaTreeType.Record, JavaTreeType.Class) => InnerClassOfClassAllowedModifiers
      case (JavaTreeType.Class | JavaTreeType.Record, JavaTreeType.Interface) => InnerClassOfInterfaceAllowedModifiers
      case (JavaTreeType.Interface, JavaTreeType.Package) => OuterInterfaceAllowedModifiers
      case (JavaTreeType.Interface, JavaTreeType.Class) => InnerInterfaceOfClassAllowedModifiers
      case (JavaTreeType.Interface, JavaTreeType.Interface) => InnerInterfaceOfInterfaceAllowedModifiers
      case (JavaTreeType.Method, JavaTreeType.Class) => ClassMethodAllowedModifiers
      case (JavaTreeType.Method, JavaTreeType.Interface) => InterfaceMethodAllowedModifiers
      case (JavaTreeType.Variable, JavaTreeType.Class) => ClassVariableAllowedModifiers
      case (JavaTreeType.Variable, JavaTreeType.Method | JavaTreeType.Lambda) => LocalVariableAllowedModifiers
      case (JavaTreeType.Parameter, _) => ParameterAllowedModifiers
      case _ => Set.empty
    }
  }
}
