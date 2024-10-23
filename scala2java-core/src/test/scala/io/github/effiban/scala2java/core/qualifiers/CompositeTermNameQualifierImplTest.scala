package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class CompositeTermNameQualifierImplTest extends UnitTestSuite {
  private val inheritedTermNameQualifier = mock[InheritedTermNameQualifier]
  private val importedTermNameQualifier =  mock[ImportedTermNameQualifier]
  private val coreTermNameQualifier = mock[CoreTermNameQualifier]

  private val compositeTermNameQualifier = new CompositeTermNameQualifierImpl(
    inheritedTermNameQualifier,
    importedTermNameQualifier,
    coreTermNameQualifier)

  private val qualifiedTypeMap = Map[Type, Type](
    t"Parent1" -> t"qual1.Parent1",
    t"Parent2" -> t"qual1.Parent1"
  )

  test("qualify when qualified by importers should return qualified term") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"F"
    val expectedQualifiedTerm = q"d.F"

    val context = QualificationContext(importers = importers, qualifiedTypeMap = qualifiedTypeMap)

    doReturn(None).when(inheritedTermNameQualifier).qualify(eqTree(termName), eqQualificationContext(context))
    doReturn(Some(expectedQualifiedTerm))
      .when(importedTermNameQualifier).qualify(eqTree(termName), eqTreeList(importers))

    compositeTermNameQualifier.qualify(termName, context).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when qualified by core qualifier should return qualified term") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"Int"
    val expectedQualifiedTerm = q"scala.Int"

    val context = QualificationContext(importers = importers, qualifiedTypeMap = qualifiedTypeMap)

    doReturn(None).when(inheritedTermNameQualifier).qualify(eqTree(termName), eqQualificationContext(context))
    doReturn(None).when(importedTermNameQualifier).qualify(eqTree(termName), eqTo(importers))
    doReturn(Some(expectedQualifiedTerm)).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, context).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when not qualified by any qualifier - should return unchanged") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"Foo"

    val context = QualificationContext(importers = importers, qualifiedTypeMap = qualifiedTypeMap)

    doReturn(None).when(inheritedTermNameQualifier).qualify(eqTree(termName), eqQualificationContext(context))
    doReturn(None).when(importedTermNameQualifier).qualify(eqTree(termName), eqTo(importers))
    doReturn(None).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, context).structure shouldBe termName.structure
  }

  test("qualify when Term.Name has a parent Object should return the name") {
    val anObject =
      q"""
      object A {
      }
      """

    val importers = List(importer"a.A")

    val context = QualificationContext(importers = importers, qualifiedTypeMap = qualifiedTypeMap)

    compositeTermNameQualifier.qualify(anObject.name, context).structure shouldBe anObject.name.structure
  }

  test("qualify when Term.Name has a parent Object should not invoke inherited qualifier") {
    val anObject =
      q"""
      object A {
      }
      """

    compositeTermNameQualifier.qualify(anObject.name, QualificationContext())

    verifyNoInteractions(inheritedTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Object should not invoke imported qualifier") {
    val anObject =
      q"""
      object A {
      }
      """

    compositeTermNameQualifier.qualify(anObject.name, QualificationContext())

    verifyNoInteractions(importedTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Object should not invoke core qualifier") {
    val anObject =
      q"""
      object A {
      }
      """

    compositeTermNameQualifier.qualify(anObject.name, QualificationContext())

    verifyNoInteractions(coreTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Decl.Def should return the name") {
    val declDef = q"def foo()"

    val importers = List(importer"a.foo")

    val context = QualificationContext(importers = importers, qualifiedTypeMap = qualifiedTypeMap)

    compositeTermNameQualifier.qualify(declDef.name, context).structure shouldBe declDef.name.structure
  }

  test("qualify when Term.Name has a parent Decl.Def should not invoke inherited qualifier") {
    val declDef = q"def foo()"

    compositeTermNameQualifier.qualify(declDef.name, QualificationContext())

    verifyNoInteractions(inheritedTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Decl.Def should not invoke imported qualifier") {
    val declDef = q"def foo()"

    compositeTermNameQualifier.qualify(declDef.name, QualificationContext())

    verifyNoInteractions(importedTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Decl.Def should not invoke core qualifier") {
    val declDef = q"def foo()"

    compositeTermNameQualifier.qualify(declDef.name, QualificationContext())

    verifyNoInteractions(coreTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Term.Param should return the name") {
    val termParam = param"x: Int"

    val importers = List(importer"x.X")

    val context = QualificationContext(importers = importers, qualifiedTypeMap = qualifiedTypeMap)

    compositeTermNameQualifier.qualify(termParam.name.asInstanceOf[Term.Name], context).structure shouldBe
      termParam.name.structure
  }

  test("qualify when Term.Name has a parent Term.Param should not invoke inherited qualifier") {
    val termParam = param"x: Int"

    compositeTermNameQualifier.qualify(termParam.name.asInstanceOf[Term.Name], QualificationContext())

    verifyNoInteractions(inheritedTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Term.Param should not invoke imported qualifier") {
    val termParam = param"x: Int"

    compositeTermNameQualifier.qualify(termParam.name.asInstanceOf[Term.Name], QualificationContext())

    verifyNoInteractions(importedTermNameQualifier)
  }

  test("qualify when Term.Name has a parent Term.Param should not invoke core qualifier") {
    val termParam = param"x: Int"

    compositeTermNameQualifier.qualify(termParam.name.asInstanceOf[Term.Name], QualificationContext())

    verifyNoInteractions(coreTermNameQualifier)
  }
}
