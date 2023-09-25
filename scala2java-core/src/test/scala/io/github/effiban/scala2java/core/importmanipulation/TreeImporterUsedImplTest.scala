package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TreeImporterUsedImplTest extends UnitTestSuite {

  private val typeNameImporterMatcher = mock[TypeNameImporterMatcher]

  private val treeImporterUsed = new TreeImporterUsedImpl(typeNameImporterMatcher)


  test("apply() for a 'Decl.Var' when has a matching Type.Name should return true") {
    val importer = importer"x.X"

    doReturn(Some(importer)).when(typeNameImporterMatcher).findMatch(eqTree(t"X"), eqTree(importer))

    treeImporterUsed(q"var x: X", importer) shouldBe true
  }

  test("apply() for a 'Decl.Var' when has no matching Type.Name should return false") {
    val importer = importer"y.Y"

    doReturn(None).when(typeNameImporterMatcher).findMatch(eqTree(t"X"), eqTree(importer))

    treeImporterUsed(q"var x: X", importer) shouldBe false
  }

  test("apply() for a Class when has a matching nested Type.Name should return true") {
    val theClass =
      q"""
      class A {
        var b: B
      }
      """

    val importer = importer"b.B"

    doReturn(Some(importer)).when(typeNameImporterMatcher).findMatch(eqTree(t"B"), eqTree(importer))

    treeImporterUsed(theClass, importer) shouldBe true
  }

  test("apply() for a Class when has no matching nested trees should return false") {
    val theClass =
      q"""
      class A {
        var b: B
      }
      """

    val importer = importer"c.C"

    doReturn(None).when(typeNameImporterMatcher).findMatch(eqTree(t"B"), eqTree(importer))

    treeImporterUsed(theClass, importer) shouldBe false
  }

  test("apply() for a Trait when has a matching nested Type.Name should return true") {
    val theClass =
      q"""
      trait A {
        var b: B
      }
      """

    val importer = importer"b.B"

    doReturn(Some(importer)).when(typeNameImporterMatcher).findMatch(eqTree(t"B"), eqTree(importer))

    treeImporterUsed(theClass, importer) shouldBe true
  }

  test("apply() for a Trait when has no matching nested trees should return false") {
    val theTrait =
      q"""
      trait A {
        val b: B
      }
      """

    val importer = importer"c.C"

    doReturn(None).when(typeNameImporterMatcher).findMatch(eqTree(t"B"), eqTree(importer))

    treeImporterUsed(theTrait, importer) shouldBe false
  }

  test("apply() for a Type.Var should not try to match it and return false") {
    val importer = importer"a.b"

    treeImporterUsed(Type.Var(t"b"), importer) shouldBe false

    verifyNoInteractions(typeNameImporterMatcher)
  }

  test("apply() for the name of a Type.Param should not try to match it and return false") {
    val importer = importer"t.T"

    treeImporterUsed(tparam"T", importer) shouldBe false

    verifyNoInteractions(typeNameImporterMatcher)
  }
}
