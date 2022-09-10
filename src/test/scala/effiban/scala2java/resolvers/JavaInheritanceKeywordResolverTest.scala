package effiban.scala2java.resolvers

import effiban.scala2java.entities.{JavaKeyword, JavaTreeType}
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Init, Lit, Name, Type}

class JavaInheritanceKeywordResolverTest extends UnitTestSuite {

  test("resolve for class with parent args should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Class, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for class without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Class, inits) shouldBe JavaKeyword.Implements
  }

  test("resolve for record with parent args should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Record, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for record without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Record, inits) shouldBe JavaKeyword.Implements
  }

  test("resolve for enum with parent args should throw exception") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    intercept[IllegalStateException] {
      JavaInheritanceKeywordResolver.resolve(JavaTreeType.Enum, inits)
    }
  }

  test("resolve for enum without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Enum, inits) shouldBe JavaKeyword.Implements
  }

  test("resolve for interface should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Interface, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for unknown with parent args should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Unknown, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for unknown without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaTreeType.Unknown, inits) shouldBe JavaKeyword.Implements
  }
}
