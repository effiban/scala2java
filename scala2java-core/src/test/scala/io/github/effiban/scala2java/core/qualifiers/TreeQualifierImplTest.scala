package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaDouble, ScalaInt}
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Template, Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeQualifierImplTest extends UnitTestSuite {

  private val superSelectQualifier = mock[SuperSelectQualifier]
  private val termNameQualifier = mock[CompositeTermNameQualifier]
  private val typeNameQualifier = mock[CompositeTypeNameQualifier]
  private val templateQualifier = mock[TemplateQualifier]

  private val qualificationContext = QualificationContext(List(importer"dummy.dummy"))

  private val treeQualifier = new TreeQualifierImpl(
    superSelectQualifier,
    termNameQualifier,
    typeNameQualifier,
    templateQualifier
  )

  test("qualify when has nested Term.Names but TermNameQualifier returns unchanged, should return unchanged") {
    val tree =
      q"""
      def foo() = {
        val x = xx
        val y = yy
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext).structure shouldBe tree.structure
  }

  test("qualify when has nested Term.Names and TermNameQualifier qualifies some of them, should return those terms qualified") {
    val initialTree =
      q"""
      def foo() = {
        val x = None
        val y = Nil
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Term.Select 'g.h' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        val x = g.h
        val y = ZZ.WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Term.Select 'g.h.i' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        val x = g.h.i
        val y = ZZ.WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Term.Select 'g.h' should NOT try to qualify 'h'") {
    val tree =
      q"""
      def foo() = {
        val x = g.h
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext)

    verify(termNameQualifier, never).qualify(eqTree(q"h"), eqQualificationContext(qualificationContext))
  }

  test("qualify when has a nested Term.Select 'super.a' should return result of superSelectQualifier") {
    val initialTree =
      q"""
      def foo() = {
        val x = super.a
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
        val x = B.super[C].a
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doReturn(q"B.super[C].a")
      .when(superSelectQualifier).qualify(eqTree(q"super"), eqTree(q"a"), eqQualificationContext(qualificationContext))

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has nested Type.Names but TypeNameQualifier returns nothing, should return unchanged") {
    val tree = q"var x: T"

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext).structure shouldBe tree.structure
  }

  test("qualify when TypeNameQualifier qualifies some of the nested types, should return those types qualified") {
    val initialTree =
      q"""
      def foo() = {
        val x: Int = 2
        val y: Double = 3.3
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has nested Type.Selects but for the first term of all, TermNameQualifier returns nothing - should return unchanged") {
    val tree =
      q"""
      def foo() = {
        var x: CC.DD
        var y: EE.FF
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext).structure shouldBe tree.structure
  }

  test("qualify when has a nested Type.Select 'g.H' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        var x: g.H
        var y: ZZ.WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Type.Select 'g.h.I' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        var x: g.h.I
        var y: ZZ.WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Type.Select 'g.H' should NOT try to qualify 'H'") {
    val initialTree =
      q"""
      def foo() = {
        var x: g.H
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(initialTree, qualificationContext)

    verify(termNameQualifier, never).qualify(eqTree(q"H"), eqQualificationContext(qualificationContext))
  }

  test("qualify when has nested Type.Projects but for the first term of all, TypeNameQualifier returns nothing - should return unchanged") {
    val tree =
      q"""
      def foo() = {
        var x: CC#DD
        var y: EE#FF
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext).structure shouldBe tree.structure
  }

  test("qualify when has a nested Type.Project 'G#H' and TypeNameQualifier qualifies 'G' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        var x: G#H
        var y: ZZ#WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Type.Project 'g.H#I' and TypeNameQualifier qualifies 'g' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        var x: g.H#I
        var y: ZZ#WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Type.Project 'G#H#I' and TypeNameQualifier qualifies 'G' should return it qualified") {
    val initialTree =
      q"""
      def foo() = {
        var x: G#H#I
        var y: ZZ#WW
      }
      """

    val expectedFinalTree =
      q"""
      def foo() = {
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

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }

  test("qualify when has a nested Type.Project 'G#H' should NOT try to qualify 'H'") {
    val tree =
      q"""
      def foo() = {
        var x: G#H
      }
      """

    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(qualificationContext))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext)

    verify(typeNameQualifier, never).qualify(eqTree(t"H"), eqQualificationContext(qualificationContext))
  }

  test("qualify when has a nested Template but TemplateQualifier returns unchanged, should return unchanged") {
    val tree =
      q"""
      class A extends B with C {
      }
      """

    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))
    doAnswer((template: Template) => template)
      .when(templateQualifier).qualify(any[Template], eqQualificationContext(qualificationContext))

    treeQualifier.qualify(tree, qualificationContext).structure shouldBe tree.structure
  }

  test("qualify when has a nested Template and TemplateQualifier qualifies it, should return it qualified") {
    val initialTree =
      q"""
      class A extends B with C {
        var d: D
      }
      """

    val initialTemplate =
      template"""
      B with C {
        var d: D
      }
      """

    val expectedFinalTemplate =
      template"""
      qualB.B with qualC.C {
        var d: qualD.D
      }
      """

    val expectedFinalTree =
      q"""
      class A extends qualB.B with qualC.C {
        var d: qualD.D
      }
      """

    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(qualificationContext))
    doAnswer(expectedFinalTemplate)
      .when(templateQualifier).qualify(eqTree(initialTemplate), eqQualificationContext(qualificationContext))

    treeQualifier.qualify(initialTree, qualificationContext).structure shouldBe expectedFinalTree.structure
  }
}
