package effiban.scala2java.resolvers

import effiban.scala2java.entities.{JavaKeyword, JavaScope}
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Init, Lit, Name, Type}

class JavaInheritanceKeywordResolverTest extends UnitTestSuite {

  test("resolve for class with parent args should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Class, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for class without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Class, inits) shouldBe JavaKeyword.Implements
  }

  test("resolve for interface should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Interface, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for unknown with parent args should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.NoScope, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for unknown without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.NoScope, inits) shouldBe JavaKeyword.Implements
  }
}
