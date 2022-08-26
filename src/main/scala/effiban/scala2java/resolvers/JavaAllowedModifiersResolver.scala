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

  private final val InnerClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val OuterInterfaceAllowedModifiers = Set[JavaModifier](JavaModifier.Public, JavaModifier.Sealed)

  private final val InnerInterfaceAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Sealed
  )

  private final val ClassMethodAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val InterfaceMethodAllowedModifiers = Set[JavaModifier](JavaModifier.Private, JavaModifier.Default)

  private final val ClassDataMemberAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Final
  )

  override def resolve(treeType: JavaTreeType, scope: JavaTreeType): Set[JavaModifier] = {

    (treeType, scope) match {
      case (JavaTreeType.Class, JavaTreeType.Package)     => OuterClassAllowedModifiers
      case (JavaTreeType.Class, _)                        => InnerClassAllowedModifiers
      case (JavaTreeType.Interface, JavaTreeType.Package) => OuterInterfaceAllowedModifiers
      case (JavaTreeType.Interface, _)                    => InnerInterfaceAllowedModifiers
      case (JavaTreeType.Method, JavaTreeType.Class)      => ClassMethodAllowedModifiers
      case (JavaTreeType.Method, JavaTreeType.Interface)  => InterfaceMethodAllowedModifiers
      case (JavaTreeType.DataMember, JavaTreeType.Class)  => ClassDataMemberAllowedModifiers
      case _                                              => Set.empty
    }
  }
}
