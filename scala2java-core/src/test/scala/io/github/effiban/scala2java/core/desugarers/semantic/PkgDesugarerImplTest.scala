package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers.any

import scala.meta.{Stat, XtensionQuasiquoteTerm}

class PkgDesugarerImplTest extends UnitTestSuite {

  private val statDesugarer = mock[StatDesugarer]

  private val pkgDesugarer = new PkgDesugarerImpl(statDesugarer)

  test("desugar") {
    val objC =
      q"""
      object C {
        val x = func
      }
      """

    val objD =
      q"""
      object D {
        val y = func
      }
      """

    val pkg =
      q"""
      package a.b {
        object C {
          val x = func
        }
        object D {
          val y = func
        }
      }
      """

    val desugaredObjC =
      q"""
      object C {
        val x = func()
      }
      """

    val desugaredObjD =
      q"""
      object D {
        val y = func()
      }
      """

    val desugaredPkg =
      q"""
      package a.b {
        object C {
          val x = func()
        }
        object D {
          val y = func()
        }
      }
      """

    doAnswer( (stat: Stat) => stat match {
      case aStat: Stat if aStat.structure == objC.structure => desugaredObjC
      case aStat: Stat if aStat.structure == objD.structure => desugaredObjD
      case aStat => aStat
    }).when(statDesugarer).desugar(any[Stat])

    pkgDesugarer.desugar(pkg).structure shouldBe desugaredPkg.structure

  }

}
