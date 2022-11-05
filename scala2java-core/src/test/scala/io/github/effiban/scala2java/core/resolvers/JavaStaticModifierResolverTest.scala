package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier.Static
import io.github.effiban.scala2java.core.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

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

  private val context = mock[ModifiersContext]

  forAll(ResolverScenarios) { case (javaTreeType: JavaTreeType, javaScope: JavaScope, expectedResult: Boolean) =>
    test(s"A Java type '$javaTreeType' with a Java scope of '$javaScope' should ${if (expectedResult) "" else "not"} require 'static'") {
      when(context.javaTreeType).thenReturn(javaTreeType)
      when(context.javaScope).thenReturn(javaScope)

      JavaStaticModifierResolver.resolve(context) shouldBe (if (expectedResult) Some(Static) else None)
    }
  }

}
