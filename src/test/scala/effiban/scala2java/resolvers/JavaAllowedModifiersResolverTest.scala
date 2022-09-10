package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.testsuites.UnitTestSuite

class JavaAllowedModifiersResolverTest extends UnitTestSuite {

  private final val ExpectedOuterClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val ExpectedInnerClassOfClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val ExpectedInnerClassOfInterfaceAllowedModifiers = Set[JavaModifier](
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val ExpectedOuterInterfaceAllowedModifiers = Set[JavaModifier](JavaModifier.Public, JavaModifier.Sealed)

  private final val ExpectedInnerInterfaceOfClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Sealed
  )

  private final val ExpectedInnerInterfaceOfInterfaceAllowedModifiers = Set[JavaModifier](JavaModifier.Sealed)

  private final val ExpectedClassMethodAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val ExpectedInterfaceMethodAllowedModifiers = Set[JavaModifier](JavaModifier.Private, JavaModifier.Default)

  private final val ExpectedClassVariableAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Final
  )

  private final val ExpectedLocalVariableAllowedModifiers = Set[JavaModifier](JavaModifier.Final)

  private final val ExpectedParameterAllowedModifiers = Set[JavaModifier](JavaModifier.Final)


  private val AllowedModifiersScenarios = Table(
    ("JavaTreeType", "JavaScope", "ExpectedAllowedModifiers"),
    (JavaTreeType.Class, JavaTreeType.Package, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Class, JavaTreeType.Class, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Class, JavaTreeType.Enum, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Class, JavaTreeType.Interface, ExpectedInnerClassOfInterfaceAllowedModifiers),
    (JavaTreeType.Record, JavaTreeType.Package, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Record, JavaTreeType.Class, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Record, JavaTreeType.Enum, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Record, JavaTreeType.Interface, ExpectedInnerClassOfInterfaceAllowedModifiers),
    (JavaTreeType.Enum, JavaTreeType.Package, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Enum, JavaTreeType.Class, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Enum, JavaTreeType.Interface, ExpectedInnerClassOfInterfaceAllowedModifiers),
    (JavaTreeType.Interface, JavaTreeType.Package, ExpectedOuterInterfaceAllowedModifiers),
    (JavaTreeType.Interface, JavaTreeType.Class, ExpectedInnerInterfaceOfClassAllowedModifiers),
    (JavaTreeType.Interface, JavaTreeType.Interface, ExpectedInnerInterfaceOfInterfaceAllowedModifiers),
    (JavaTreeType.Method, JavaTreeType.Class, ExpectedClassMethodAllowedModifiers),
    (JavaTreeType.Method, JavaTreeType.Interface, ExpectedInterfaceMethodAllowedModifiers),
    (JavaTreeType.Variable, JavaTreeType.Class, ExpectedClassVariableAllowedModifiers),
    (JavaTreeType.Variable, JavaTreeType.Interface, Set.empty),
    (JavaTreeType.Variable, JavaTreeType.Method, ExpectedLocalVariableAllowedModifiers),
    (JavaTreeType.Variable, JavaTreeType.Lambda, ExpectedLocalVariableAllowedModifiers),
    (JavaTreeType.Parameter, JavaTreeType.Class, ExpectedParameterAllowedModifiers),
    (JavaTreeType.Parameter, JavaTreeType.Method, ExpectedParameterAllowedModifiers),
    (JavaTreeType.Parameter, JavaTreeType.Lambda, ExpectedParameterAllowedModifiers)
  )

  forAll(AllowedModifiersScenarios) { case (treeType: JavaTreeType, scope: JavaTreeType, expectedAllowedModifiers: Set[JavaModifier]) =>
    test(s"Java '$treeType' in scope '$scope' should allow: $expectedAllowedModifiers") {
      JavaAllowedModifiersResolver.resolve(treeType, scope) shouldBe expectedAllowedModifiers
    }
  }
}
