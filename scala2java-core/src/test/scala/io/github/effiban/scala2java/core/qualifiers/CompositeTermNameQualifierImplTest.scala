package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class CompositeTermNameQualifierImplTest extends UnitTestSuite {
  private val inheritedTermNameQualifier = mock[InheritedTermNameQualifier]
  private val importedTermNameQualifier =  mock[ImportedTermNameQualifier]
  private val coreTermNameQualifier = mock[CoreTermNameQualifier]

  private val compositeTermNameQualifier = new CompositeTermNameQualifierImpl(
    inheritedTermNameQualifier,
    importedTermNameQualifier,
    coreTermNameQualifier)


  test("qualify when qualified by importers should return qualified term") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"F"
    val expectedQualifiedTerm = q"d.F"

    doReturn(None).when(inheritedTermNameQualifier).qualify(eqTree(termName))
    doReturn(Some(expectedQualifiedTerm))
      .when(importedTermNameQualifier).qualify(eqTree(termName), eqTreeList(importers))

    compositeTermNameQualifier.qualify(termName, QualificationContext(importers)).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when qualified by core qualifier should return qualified term") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"Int"
    val expectedQualifiedTerm = q"scala.Int"

    doReturn(None).when(inheritedTermNameQualifier).qualify(eqTree(termName))
    doReturn(None).when(importedTermNameQualifier).qualify(eqTree(termName), eqTo(importers))
    doReturn(Some(expectedQualifiedTerm)).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, QualificationContext(importers)).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when not qualified by any qualifier - should return unchanged") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"Foo"

    doReturn(None).when(inheritedTermNameQualifier).qualify(eqTree(termName))
    doReturn(None).when(importedTermNameQualifier).qualify(eqTree(termName), eqTo(importers))
    doReturn(None).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, QualificationContext(importers)).structure shouldBe termName.structure
  }

  test("qualify when Term.Name has a parent Object should return the name") {
    val anObject =
      q"""
      object A {
      }
      """

    val importers = List(importer"a.A")

    compositeTermNameQualifier.qualify(anObject.name, QualificationContext(importers)).structure shouldBe anObject.name.structure
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

    compositeTermNameQualifier.qualify(declDef.name, QualificationContext(importers)).structure shouldBe declDef.name.structure
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

    compositeTermNameQualifier.qualify(termParam.name.asInstanceOf[Term.Name], QualificationContext(importers)).structure shouldBe
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
