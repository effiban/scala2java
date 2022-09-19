package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.testsuites.UnitTestSuite

class JavaStaticModifierResolverTest extends UnitTestSuite {

  private val ResolverScenarios = Table(
    ("JavaTreeType", "JavaScope", "ExpectedResult"),
    (JavaTreeType.Class, JavaScope.UtilityClass, true),
    (JavaTreeType.Class, JavaScope.Package, false),
    (JavaTreeType.Record, JavaScope.UtilityClass, true),
    (JavaTreeType.Record, JavaScope.Class, false),
    (JavaTreeType.Enum, JavaScope.UtilityClass, true),
    (JavaTreeType.Enum, JavaScope.Class, false),
    (JavaTreeType.Interface, JavaScope.UtilityClass, true),
    (JavaTreeType.Interface, JavaScope.Interface, false),
    (JavaTreeType.Variable, JavaScope.UtilityClass, true),
    (JavaTreeType.Variable, JavaScope.Block, false)
  )

  private val context = mock[JavaModifiersContext]

  forAll(ResolverScenarios) { case (javaTreeType: JavaTreeType, javaScope: JavaScope, expectedResult: Boolean) =>
    test(s"A Java type '$javaTreeType' with a Java scope of '$javaScope' should ${if (expectedResult) "" else "not"} require 'static'") {
      when(context.javaTreeType).thenReturn(javaTreeType)
      when(context.javaScope).thenReturn(javaScope)

      JavaStaticModifierResolver.resolve(context) shouldBe (if (expectedResult) Some(JavaModifier.Static) else None)
    }
  }

}
