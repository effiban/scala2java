package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaDouble, ScalaInt}
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class StatQualifierImplTest extends UnitTestSuite {

  private val termNameQualifier = mock[CompositeTermNameQualifier]
  private val typeNameQualifier = mock[CompositeTypeNameQualifier]

  private val qualificationContext = QualificationContext(List(importer"dummy.dummy"))

  private val statQualifier = new StatQualifierImpl(
    termNameQualifier,
    typeNameQualifier
  )

  test("qualify when has nested Term.Names but TermNameQualifier returns unchanged, should return unchanged") {
    val stat =
      q"""
      object A {
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(stat, qualificationContext).structure shouldBe stat.structure
  }

  test("qualify when has nested Term.Names and TermNameQualifier qualifies some of them, should return those terms qualified") {
    val initialStat =
      q"""
      object C {
        val x = None
        val y = Nil
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        val x = scala.None
        val y = scala.Nil
      }
      """

    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"None".structure => ScalaNone
      case aTermName if aTermName.structure == q"Nil".structure => ScalaNil
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Term.Select 'g.h' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialStat =
      q"""
      object C {
        val x = g.h
        val y = ZZ.WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        val x = qual.g.h
        val y = ZZ.WW
      }
      """

    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Term.Select 'g.h.i' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialStat =
      q"""
      object C {
        val x = g.h.i
        val y = ZZ.WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        val x = qual.g.h.i
        val y = ZZ.WW
      }
      """

    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Term.Select 'g.h' should NOT try to qualify 'h'") {
    val initialStat =
      q"""
      object C {
        val x = g.h
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext)

    verify(termNameQualifier, never).qualify(eqTree(q"h"), eqQualificationContext(qualificationContext))
  }

  test("qualify when has nested Type.Names but TypeNameQualifier returns nothing, should return unchanged") {
    val stat =
      q"""
      trait G {
      }
      """

    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(stat, qualificationContext).structure shouldBe stat.structure
  }

  test("qualify when TypeNameQualifier qualifies some of the nested types, should return those types qualified") {
    val initialStat =
      q"""
      trait C {
        val x: Int = 2
        val y: Double = 3.3
      }
      """

    val expectedFinalStat =
      q"""
      trait C {
        val x: scala.Int = 2
        val y: scala.Double = 3.3
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"Int".structure => ScalaInt
      case aTypeName if aTypeName.structure == t"Double".structure => ScalaDouble
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has nested Type.Selects but for the first term of all, TermNameQualifier returns nothing - should return unchanged") {
    val stat =
      q"""
      object A {
        var x: CC.DD
        var y: EE.FF
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(stat, qualificationContext).structure shouldBe stat.structure
  }

  test("qualify when has a nested Type.Select 'g.H' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialStat =
      q"""
      object C {
        var x: g.H
        var y: ZZ.WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        var x: qual.g.H
        var y: ZZ.WW
      }
      """

    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Type.Select 'g.h.I' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialStat =
      q"""
      object C {
        var x: g.h.I
        var y: ZZ.WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        var x: qual.g.h.I
        var y: ZZ.WW
      }
      """

    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Type.Select 'g.H' should NOT try to qualify 'H'") {
    val initialStat =
      q"""
      object C {
        var x: g.H
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext)

    verify(termNameQualifier, never).qualify(eqTree(q"H"), eqQualificationContext(qualificationContext))
  }

  test("qualify when has nested Type.Projects but for the first term of all, TypeNameQualifier returns nothing - should return unchanged") {
    val stat =
      q"""
      object A {
        var x: CC#DD
        var y: EE#FF
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(stat, qualificationContext).structure shouldBe stat.structure
  }

  test("qualify when has a nested Type.Project 'G#H' and TypeNameQualifier qualifies 'G' should return it qualified") {
    val initialStat =
      q"""
      object C {
        var x: G#H
        var y: ZZ#WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        var x: qual.G#H
        var y: ZZ#WW
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"G".structure => t"qual.G"
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Type.Project 'g.H#I' and TypeNameQualifier qualifies 'g' should return it qualified") {
    val initialStat =
      q"""
      object C {
        var x: g.H#I
        var y: ZZ#WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        var x: qual.g.H#I
        var y: ZZ#WW
      }
      """

    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Type.Project 'G#H#I' and TypeNameQualifier qualifies 'G' should return it qualified") {
    val initialStat =
      q"""
      object C {
        var x: G#H#I
        var y: ZZ#WW
      }
      """

    val expectedFinalStat =
      q"""
      object C {
        var x: qual.G#H#I
        var y: ZZ#WW
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"G".structure => t"qual.G"
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(initialStat, qualificationContext).structure shouldBe expectedFinalStat.structure
  }

  test("qualify when has a nested Type.Project 'G#H' should NOT try to qualify 'H'") {
    val stat =
      q"""
      object C {
        var x: G#H
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    statQualifier.qualify(stat, qualificationContext)

    verify(typeNameQualifier, never).qualify(eqTree(t"H"), eqQualificationContext(qualificationContext))
  }
}
