package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import io.github.effiban.scala2java.testsuites.UnitTestSuite

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


  private val AllowedModifiersScenarios = Table[JavaTreeType, JavaScope, Set[JavaModifier]](
    ("JavaTreeType", "JavaScope", "ExpectedAllowedModifiers"),
    (JavaTreeType.Class, JavaScope.Package, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Class, JavaScope.Sealed, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Class, JavaScope.Class, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Class, JavaScope.UtilityClass, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Class, JavaScope.Enum, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Class, JavaScope.Interface, ExpectedInnerClassOfInterfaceAllowedModifiers),
    (JavaTreeType.Record, JavaScope.Package, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Record, JavaScope.Sealed, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Record, JavaScope.Class, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Record, JavaScope.UtilityClass, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Record, JavaScope.Enum, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Record, JavaScope.Interface, ExpectedInnerClassOfInterfaceAllowedModifiers),
    (JavaTreeType.Enum, JavaScope.Package, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Enum, JavaScope.Sealed, ExpectedOuterClassAllowedModifiers),
    (JavaTreeType.Enum, JavaScope.Class, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Enum, JavaScope.UtilityClass, ExpectedInnerClassOfClassAllowedModifiers),
    (JavaTreeType.Enum, JavaScope.Interface, ExpectedInnerClassOfInterfaceAllowedModifiers),
    (JavaTreeType.Interface, JavaScope.Package, ExpectedOuterInterfaceAllowedModifiers),
    (JavaTreeType.Interface, JavaScope.Sealed, ExpectedOuterInterfaceAllowedModifiers),
    (JavaTreeType.Interface, JavaScope.Class, ExpectedInnerInterfaceOfClassAllowedModifiers),
    (JavaTreeType.Interface, JavaScope.UtilityClass, ExpectedInnerInterfaceOfClassAllowedModifiers),
    (JavaTreeType.Interface, JavaScope.Interface, ExpectedInnerInterfaceOfInterfaceAllowedModifiers),
    (JavaTreeType.Method, JavaScope.Class, ExpectedClassMethodAllowedModifiers),
    (JavaTreeType.Method, JavaScope.Interface, ExpectedInterfaceMethodAllowedModifiers),
    (JavaTreeType.Variable, JavaScope.Class, ExpectedClassVariableAllowedModifiers),
    (JavaTreeType.Variable, JavaScope.Interface, Set.empty),
    (JavaTreeType.Variable, JavaScope.Block, ExpectedLocalVariableAllowedModifiers),
    (JavaTreeType.Parameter, JavaScope.Class, ExpectedParameterAllowedModifiers),
    (JavaTreeType.Parameter, JavaScope.MethodSignature, ExpectedParameterAllowedModifiers),
    (JavaTreeType.Parameter, JavaScope.LambdaSignature, ExpectedParameterAllowedModifiers)
  )

  forAll(AllowedModifiersScenarios) { case (treeType: JavaTreeType, scope: JavaScope, expectedAllowedModifiers: Set[JavaModifier]) =>
    test(s"Java '$treeType' in scope '$scope' should allow: $expectedAllowedModifiers") {
      JavaAllowedModifiersResolver.resolve(treeType, scope) shouldBe expectedAllowedModifiers
    }
  }
}
