package effiban.scala2java.resolvers

import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.resolvers.JavaAllowedModifiersResolver.resolve
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

  test("resolve() for outer class") {
    resolve(JavaTreeType.Class, JavaTreeType.Package) shouldBe ExpectedOuterClassAllowedModifiers
  }

  test("resolve() for inner class of class") {
    resolve(JavaTreeType.Class, JavaTreeType.Class) shouldBe ExpectedInnerClassOfClassAllowedModifiers
  }

  test("resolve() for inner class of interface") {
    resolve(JavaTreeType.Class, JavaTreeType.Interface) shouldBe ExpectedInnerClassOfInterfaceAllowedModifiers
  }

  test("resolve() for outer interface") {
    resolve(JavaTreeType.Interface, JavaTreeType.Package) shouldBe ExpectedOuterInterfaceAllowedModifiers
  }

  test("resolve() for inner interface of class") {
    resolve(JavaTreeType.Interface, JavaTreeType.Class) shouldBe ExpectedInnerInterfaceOfClassAllowedModifiers
  }

  test("resolve() for inner interface of interface") {
    resolve(JavaTreeType.Interface, JavaTreeType.Interface) shouldBe ExpectedInnerInterfaceOfInterfaceAllowedModifiers
  }

  test("resolve() for class method") {
    resolve(JavaTreeType.Method, JavaTreeType.Class) shouldBe ExpectedClassMethodAllowedModifiers
  }

  test("resolve() for interface method") {
    resolve(JavaTreeType.Method, JavaTreeType.Interface) shouldBe ExpectedInterfaceMethodAllowedModifiers
  }

  test("resolve() for class variable") {
    resolve(JavaTreeType.Variable, JavaTreeType.Class) shouldBe ExpectedClassVariableAllowedModifiers
  }

  test("resolve() for interface variable should return empty") {
    resolve(JavaTreeType.Variable, JavaTreeType.Interface) shouldBe Set.empty
  }

  test("resolve() for method variable") {
    resolve(JavaTreeType.Variable, JavaTreeType.Method) shouldBe ExpectedLocalVariableAllowedModifiers
  }

  test("resolve() for lambda variable") {
    resolve(JavaTreeType.Variable, JavaTreeType.Lambda) shouldBe ExpectedLocalVariableAllowedModifiers
  }

  test("resolve() for class (ctor.) parameter") {
    resolve(JavaTreeType.Parameter, JavaTreeType.Class) shouldBe ExpectedParameterAllowedModifiers
  }

  test("resolve() for method parameter") {
    resolve(JavaTreeType.Parameter, JavaTreeType.Method) shouldBe ExpectedParameterAllowedModifiers
  }

  test("resolve() for lambda parameter") {
    resolve(JavaTreeType.Parameter, JavaTreeType.Lambda) shouldBe ExpectedParameterAllowedModifiers
  }
}
