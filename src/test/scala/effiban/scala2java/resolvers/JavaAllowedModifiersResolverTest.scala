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

  private final val ExpectedInnerClassAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Sealed,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val ExpectedOuterInterfaceAllowedModifiers = Set[JavaModifier](JavaModifier.Public, JavaModifier.Sealed)

  private final val ExpectedInnerInterfaceAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Sealed
  )

  private final val ExpectedClassMethodAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Abstract,
    JavaModifier.Final
  )

  private final val ExpectedInterfaceMethodAllowedModifiers = Set[JavaModifier](JavaModifier.Private, JavaModifier.Default)

  private final val ExpectedClassDataMemberAllowedModifiers = Set[JavaModifier](
    JavaModifier.Private,
    JavaModifier.Protected,
    JavaModifier.Public,
    JavaModifier.Static,
    JavaModifier.Final
  )

  test("resolve() for outer class") {
    resolve(JavaTreeType.Class, JavaTreeType.Package) shouldBe ExpectedOuterClassAllowedModifiers
  }

  test("resolve() for inner class of class") {
    resolve(JavaTreeType.Class, JavaTreeType.Class) shouldBe ExpectedInnerClassAllowedModifiers
  }

  test("resolve() for inner class of interface") {
    resolve(JavaTreeType.Class, JavaTreeType.Interface) shouldBe ExpectedInnerClassAllowedModifiers
  }

  test("resolve() for outer interface") {
    resolve(JavaTreeType.Interface, JavaTreeType.Package) shouldBe ExpectedOuterInterfaceAllowedModifiers
  }

  test("resolve() for inner interface of class") {
    resolve(JavaTreeType.Interface, JavaTreeType.Class) shouldBe ExpectedInnerInterfaceAllowedModifiers
  }

  test("resolve() for inner interface of interface") {
    resolve(JavaTreeType.Interface, JavaTreeType.Interface) shouldBe ExpectedInnerInterfaceAllowedModifiers
  }

  test("resolve() for class method") {
    resolve(JavaTreeType.Method, JavaTreeType.Class) shouldBe ExpectedClassMethodAllowedModifiers
  }

  test("resolve() for interface method") {
    resolve(JavaTreeType.Method, JavaTreeType.Interface) shouldBe ExpectedInterfaceMethodAllowedModifiers
  }

  test("resolve() for class data member") {
    resolve(JavaTreeType.DataMember, JavaTreeType.Class) shouldBe ExpectedClassDataMemberAllowedModifiers
  }

  test("resolve() for interface data member should return empty") {
    resolve(JavaTreeType.DataMember, JavaTreeType.Interface) shouldBe Set.empty
  }
}
