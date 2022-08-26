package effiban.scala2java.resolvers

import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.resolvers.JavaModifierImplyingPublicResolver.resolve
import effiban.scala2java.testsuites.UnitTestSuite

class JavaModifierImplyingPublicResolverTest extends UnitTestSuite {

  test("resolve() for outer class should return 'public'") {
    resolve(JavaTreeType.Class, JavaTreeType.Package).value shouldBe JavaModifier.Public
  }

  test("resolve() for inner class should return 'public'") {
    resolve(JavaTreeType.Class, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for outer interface should return 'public'") {
    resolve(JavaTreeType.Interface, JavaTreeType.Package).value shouldBe JavaModifier.Public
  }

  test("resolve() for inner interface should return 'public'") {
    resolve(JavaTreeType.Interface, JavaTreeType.Interface).value shouldBe JavaModifier.Public
  }

  test("resolve() for class method should return 'public'") {
    resolve(JavaTreeType.Method, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for interface method definition should return 'default'") {
    resolve(JavaTreeType.Method, JavaTreeType.Interface, hasBody = true).value shouldBe JavaModifier.Default
  }

  test("resolve() for interface method declaration should return None") {
    resolve(JavaTreeType.Method, JavaTreeType.Interface) shouldBe None
  }

  test("resolve() for class data member should return 'public'") {
    resolve(JavaTreeType.DataMember, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for interface data member should return None") {
    resolve(JavaTreeType.DataMember, JavaTreeType.Interface) shouldBe None
  }
}
