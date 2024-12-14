package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates._

class ExtendedPredicatesTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)

  test("termSelectSupportsNoArgInvocations") {
    val termSelectSupportsNoArgInvocation1 = mock[TermSelectSupportsNoArgInvocation]
    val termSelectSupportsNoArgInvocation2 = mock[TermSelectSupportsNoArgInvocation]
    val termSelectSupportsNoArgInvocations = List(termSelectSupportsNoArgInvocation1, termSelectSupportsNoArgInvocation2)

    when(extension1.termSelectSupportsNoArgInvocation()).thenReturn(termSelectSupportsNoArgInvocation1)
    when(extension2.termSelectSupportsNoArgInvocation()).thenReturn(termSelectSupportsNoArgInvocation2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termSelectSupportsNoArgInvocations shouldBe termSelectSupportsNoArgInvocations
  }
}
