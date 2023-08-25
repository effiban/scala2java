package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaDouble, ScalaInt}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgQualifierImplTest extends UnitTestSuite {

  private val coreTypeNameQualifier = mock[CoreTypeNameQualifier]

  private val pkgQualifier = new PkgQualifierImpl(coreTypeNameQualifier)

  test("qualify when none of the nested Type.Names are predef, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        trait C {
        }
      }
      """

    doReturn(None).when(coreTypeNameQualifier).qualify(eqTree(t"C"))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has nested Type.Names that are predef, should return the qualified Type.Names") {
    val initialPkg =
      q"""
      package a.b {
        trait C {
          val x: Int = 2
          val y: Double = 3.3
        }
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        trait C {
          val x: scala.Int = 2
          val y: scala.Double = 3.3
        }
      }
      """

    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"Int".structure => Some(ScalaInt)
      case aTypeName if aTypeName.structure == t"Double".structure => Some(ScalaDouble)
      case _ => None
    }).when(coreTypeNameQualifier).qualify(any[Type.Name])

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
