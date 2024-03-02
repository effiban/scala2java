package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeImporterGeneratorImplTest extends UnitTestSuite {

  private val termApplyImporterGenerator = mock[TermApplyImporterGenerator]
  private val termSelectImporterGenerator = mock[TermSelectImporterGenerator]
  private val typeSelectImporterGenerator = mock[TypeSelectImporterGenerator]

  private val treeImporterGenerator = new TreeImporterGeneratorImpl(
    termApplyImporterGenerator,
    termSelectImporterGenerator,
    typeSelectImporterGenerator
  )

  test("generate for a class with two Term.Apply-s of Term.Name-s without args, and Importers generated for both") {
    val termApply1 = q"func1()"
    val termApply2 = q"func2()"

    val importer1 = importer"a.func1"
    val importer2 = importer"a.func2"

    val theClass =
      q"""
      class MyClass {
        def foo() = {
          func1()
          func2()
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

  test("generate for a class with two Term.Apply-s of Term.Name-s without args, and an Importer generated for one only") {
    val termApply1 = q"func1()"

    val importer1 = importer"a.func1"

    val theClass =
    q"""
    class MyClass {
      def foo() = {
        func1()
        func2()
      }
    }
    """

    doAnswer((termApply: Term.Apply) => termApply match {
      case aTermApply if aTermApply.structure == termApply1.structure => Some(importer1)
      case _ => None
    }).when(termApplyImporterGenerator).generate(any[Term.Apply])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1).structure
  }

  test("generate for a class with a Term.Apply of a Term.Name with qualified args, and Importers generated for the args only") {
    val termApply = q"func(A.b, C.d)"

    val termSelect1 = q"A.b"
    val termSelect2 = q"C.d"

    val importer1 = importer"A.b"
    val importer2 = importer"C.d"

    val theClass =
      q"""
      class MyClass {
        def foo() = {
          func(A.b, C.d)
        }
      }
      """

    doAnswer((termApply: Term.Apply) => termApply match {
      case aTermApply if aTermApply.structure == termApply.structure => None
      case _ => None
    }).when(termApplyImporterGenerator).generate(any[Term.Apply])

    doAnswer((termSelect: Term.Select) => termSelect match {
      case aTermSelect if aTermSelect.structure == termSelect1.structure => Some(importer1)
      case aTermSelect if aTermSelect.structure == termSelect2.structure => Some(importer2)
      case _ => None
    }).when(termSelectImporterGenerator).generate(any[Term.Select])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1, importer2).structure
  }

  test("generate for class with two Term.Selects, and Importers generated for both") {
    val termSelect1 = q"a.b.C"
    val termSelect2 = q"d.e.F"

    val importer1 = importer"a.b.C"
    val importer2 = importer"d.e.F"

    val theClass =
      q"""
      class MyClass {
        val x = a.b.C
        val y = d.e.F
      }
      """

    doAnswer((termSelect: Term.Select) => termSelect match {
      case aTermSelect if aTermSelect.structure == termSelect1.structure => Some(importer1)
      case aTermSelect if aTermSelect.structure == termSelect2.structure => Some(importer2)
      case _ => None
    }).when(termSelectImporterGenerator).generate(any[Term.Select])

    treeImporterGenerator.generate(theClass).structure shouldBe List(importer1, importer2).structure
  }

  test("generate for class with two Term.Selects, and an Importer generated for one only") {
    val termSelect1 = q"a.b.C"

    val importer1 = importer"a.b.C"

    val theClass =
      q"""
      class MyClass {
        val x = a.b.C
        val y = d.e.F
      }
      """

    doAnswer((termSelect: Term.Select) => termSelect match {
      case aTermSelect if aTermSelect.structure == termSelect1.structure => Some(importer1)
      case _ => None
    }).when(termSelectImporterGenerator).generate(any[Term.Select])

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

  test("generate for a package with imports and nothing else should return empty") {
    val thePackage =
      q"""
      package MyPackage {
        import a.b.c
        import d.e.f
      }
      """

    treeImporterGenerator.generate(thePackage) shouldBe Nil
  }

}
