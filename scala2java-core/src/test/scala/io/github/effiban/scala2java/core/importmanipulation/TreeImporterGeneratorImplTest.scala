package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeImporterGeneratorImplTest extends UnitTestSuite {

  private val termApplyImporterGenerator = mock[TermApplyImporterGenerator]
  private val typeSelectImporterGenerator = mock[TypeSelectImporterGenerator]

  private val treeImporterGenerator = new TreeImporterGeneratorImpl(
    termApplyImporterGenerator,
    typeSelectImporterGenerator
  )

  test("generate for a class with two Term.Apply-s, and Importers generated for both") {
    val termApply1 = q"a.B.doC()"
    val termApply2 = q"d.E.doF()"

    val importer1 = importer"a.B.doC"
    val importer2 = importer"d.E.doF"

    val theClass =
      q"""
      class MyClass {
        def foo {
          a.B.doC()
          d.E.doF()
        }
      }
      """

    doAnswer((termApply: Term.Apply) => termApply match {
      case aTermApply if aTermApply.structure == termApply1.structure => Some(importer1)
      case aTermApply if aTermApply.structure == termApply2.structure => Some(importer2)
      case _ => None
    }).when(termApplyImporterGenerator).generate(any[Term.Apply])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1, importer2).structure
  }

  test("generate for a class with two Term.Apply-s, and an Importer generated for one only") {
    val termApply1 = q"a.B.doC()"

    val importer1 = importer"a.B.doC"

    val theClass =
    q"""
    class MyClass {
      def foo {
        a.B.doC()
        d.E.doF()
      }
    }
    """

    doAnswer((termApply: Term.Apply) => termApply match {
      case aTermApply if aTermApply.structure == termApply1.structure => Some(importer1)
      case _ => None
    }).when(termApplyImporterGenerator).generate(any[Term.Apply])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1).structure
  }

  test("generate for class with two members defined using Type.Selects, and Importers generated for both") {
    val typeSelect1 = t"a.b.C"
    val typeSelect2 = t"d.e.F"

    val importer1 = importer"a.b.C"
    val importer2 = importer"d.e.F"

    val theClass =
      q"""
      class MyClass {
        val x: a.b.C
        val y: d.e.F
      }
      """

    doAnswer((typeSelect: Type.Select) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == typeSelect1.structure => Some(importer1)
      case aTypeSelect if aTypeSelect.structure == typeSelect2.structure => Some(importer2)
      case _ => None
    }).when(typeSelectImporterGenerator).generate(any[Type.Select])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1, importer2).structure
  }

  test("generate for class with two members defined using Type.Selects, and an Importer generated only for one") {
    val typeSelect1 = t"a.b.C"

    val importer1 = importer"a.b.C"

    val theClass =
      q"""
      class MyClass {
        val x: a.b.C
        val y: d.e.F
      }
      """

    doAnswer((typeSelect: Type.Select) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == typeSelect1.structure => Some(importer1)
      case _ => None
    }).when(typeSelectImporterGenerator).generate(any[Type.Select])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1).structure
  }

  test("generate for class with two members defined using Type.Names should return empty") {
    val theClass =
      q"""
      class MyClass {
        val x: X
        val y: Y
      }
      """

    treeImporterGenerator.generate(theClass) shouldBe Nil
  }
}
