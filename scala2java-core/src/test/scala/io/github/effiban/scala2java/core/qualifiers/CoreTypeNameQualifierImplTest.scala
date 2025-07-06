package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermNames.Scala
import io.github.effiban.scala2java.core.entities.TermSelects.{JavaLang, ScalaPredef}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class CoreTypeNameQualifierImplTest extends UnitTestSuite {

  private val scalaReflectionLookup = mock[ScalaReflectionLookup]

  private val coreTypeNameQualifier: CoreTypeNameQualifier = new CoreTypeNameQualifierImpl(scalaReflectionLookup)

  test(s"qualify when exists in Predef should return properly qualified type") {
    val typeName = t"Set"
    val qualifiedTypeName = t"scala.Predef.Set"
    when(scalaReflectionLookup.findModuleTypeMemberOf(eqTree(ScalaPredef), eqTree(typeName)))
      .thenReturn(Some(qualifiedTypeName))
    coreTypeNameQualifier.qualify(typeName).value.structure shouldBe qualifiedTypeName.structure
  }

  test(s"qualify when exists in Scala package should return properly qualified type") {
    val typeName = t"Option"
    val qualifiedTypeName = t"scala.Option"
    when(scalaReflectionLookup.findModuleTypeMemberOf(eqTree(Scala), eqTree(typeName)))
      .thenReturn(Some(qualifiedTypeName))
    coreTypeNameQualifier.qualify(typeName).value.structure shouldBe qualifiedTypeName.structure
  }

  test(s"qualify when exists in Java should return properly qualified type") {
    val typeName = t"IllegalArgumentException"
    val qualifiedTypeName = t"java.lang.IllegalArgumentException"
    when(scalaReflectionLookup.findModuleTypeMemberOf(eqTree(JavaLang), eqTree(typeName)))
      .thenReturn(Some(qualifiedTypeName))
    coreTypeNameQualifier.qualify(typeName).value.structure shouldBe qualifiedTypeName.structure
  }

  test("qualify() when not found should return None") {
    val typeName = t"bla"
    when(scalaReflectionLookup.findModuleTypeMemberOf(eqTree(JavaLang), eqTree(typeName))).thenReturn(None)
    coreTypeNameQualifier.qualify(typeName) shouldBe None
  }
}
