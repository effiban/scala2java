package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermNames.Scala
import io.github.effiban.scala2java.core.entities.TermSelects.{JavaLang, ScalaPredef}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CoreTermNameQualifierImplTest extends UnitTestSuite {

  private val scalaReflectionLookup = mock[ScalaReflectionLookup]

  private val coreTermNameQualifier: CoreTermNameQualifier = new CoreTermNameQualifierImpl(scalaReflectionLookup)

  test(s"qualify when exists in Predef should return properly qualified name") {
    val termName = q"Set"
    val qualifiedTermName = q"scala.Predef.Set"
    when(scalaReflectionLookup.findModuleTermMemberOf(eqTree(ScalaPredef), eqTree(termName)))
      .thenReturn(Some(qualifiedTermName))
    coreTermNameQualifier.qualify(termName).value.structure shouldBe qualifiedTermName.structure
  }

  test(s"qualify when exists in Scala package should return properly qualified name") {
    val termName = q"Option"
    val qualifiedTermName = q"scala.Option"
    when(scalaReflectionLookup.findModuleTermMemberOf(eqTree(Scala), eqTree(termName)))
      .thenReturn(Some(qualifiedTermName))
    coreTermNameQualifier.qualify(termName).value.structure shouldBe qualifiedTermName.structure
  }

  test(s"qualify when exists in Java should return properly qualified name") {
    val termName = q"IllegalArgumentException"
    val qualifiedTermName = q"java.lang.IllegalArgumentException"
    when(scalaReflectionLookup.findModuleTermMemberOf(eqTree(JavaLang), eqTree(termName)))
      .thenReturn(Some(qualifiedTermName))
    coreTermNameQualifier.qualify(termName).value.structure shouldBe qualifiedTermName.structure
  }

  test("qualify() when not found should return None") {
    val termName = q"bla"
    when(scalaReflectionLookup.findModuleTermMemberOf(eqTree(JavaLang), eqTree(termName))).thenReturn(None)
    coreTermNameQualifier.qualify(termName) shouldBe None
  }
}
