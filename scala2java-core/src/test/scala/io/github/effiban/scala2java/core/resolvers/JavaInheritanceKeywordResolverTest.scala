package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.entities.JavaKeyword.{Extends, Implements}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Init, Lit, Name, Type}

class JavaInheritanceKeywordResolverTest extends UnitTestSuite {

  test("resolve for class with parent args should return 'extends'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Class, inits) shouldBe Extends
  }

  test("resolve for class without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Class, inits) shouldBe Implements
  }

  test("resolve for utility class should throw exception") {
    intercept[IllegalStateException] {
      JavaInheritanceKeywordResolver.resolve(JavaScope.UtilityClass, Nil)
    }
  }

  test("resolve for enum with parent args should throw exception") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = List(List(Lit.Int(3))))
    )
    intercept[IllegalStateException] {
      JavaInheritanceKeywordResolver.resolve(JavaScope.Enum, inits)
    }
  }

  test("resolve for enum without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Enum, inits) shouldBe JavaKeyword.Implements
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
    JavaInheritanceKeywordResolver.resolve(JavaScope.Unknown, inits) shouldBe JavaKeyword.Extends
  }

  test("resolve for unknown without parent args should return 'implements'") {
    val inits = List(
      Init(tpe = Type.Name("Parent"), name = Name.Anonymous(), argss = Nil)
    )
    JavaInheritanceKeywordResolver.resolve(JavaScope.Unknown, inits) shouldBe JavaKeyword.Implements
  }
}
