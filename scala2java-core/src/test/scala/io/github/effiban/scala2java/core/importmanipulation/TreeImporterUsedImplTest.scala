package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TreeImporterUsedImplTest extends UnitTestSuite {

  private val termNameImporterMatcher = mock[TermNameImporterMatcher]
  private val typeNameImporterMatcher = mock[TypeNameImporterMatcher]

  private val treeImporterUsed = new TreeImporterUsedImpl(termNameImporterMatcher, typeNameImporterMatcher)

  test("apply() for a 'Defn.Var' when has a matching Term.Name in the RHS should return true") {
    val importer = importer"a.foo"

    doReturn(Some(importer)).when(termNameImporterMatcher).findMatch(eqTree(q"foo"), eqTree(importer))

    treeImporterUsed(q"var x = foo", importer) shouldBe true
  }

  test("apply() for a 'Defn.Var' when has a non-matching Term.Name in the RHS should return false") {
    val importer = importer"a.foo"

    doReturn(None).when(termNameImporterMatcher).findMatch(eqTree(q"foo"), eqTree(importer))

    treeImporterUsed(q"var x = foo", importer) shouldBe false
  }

  test("apply() for a Object when has a matching nested Term.Name should return true") {
    val theObject =
      q"""
      object A {
        final var c = foo
      }
      """

    val importer = importer"a.foo"

    doReturn(Some(importer)).when(termNameImporterMatcher).findMatch(eqTree(q"foo"), eqTree(importer))

    treeImporterUsed(theObject, importer) shouldBe true
  }

  test("apply() for an Object when has no matching nested trees should return false") {
    val theObject =
      q"""
      object A {
        final var c = foo
      }
      """

    val importer = importer"a.bla"

    doReturn(None).when(termNameImporterMatcher).findMatch(eqTree(q"foo"), eqTree(importer))

    treeImporterUsed(theObject, importer) shouldBe false
  }

  test("apply() for a Defn.Def when has a matching nested Term.Name should return true") {
    val defnDef = q"def myFunc() = { var x = foo }"

    val importer = importer"a.foo"

    doReturn(Some(importer)).when(termNameImporterMatcher).findMatch(eqTree(q"foo"), eqTree(importer))

    treeImporterUsed(defnDef, importer) shouldBe true
  }

  test("apply() for a Defn.Def when has no matching nested trees should return false") {
    val defnDef = q"def myFunc() = { var x = foo }"

    val importer = importer"a.bar"

    doReturn(None).when(termNameImporterMatcher).findMatch(eqTree(q"foo"), eqTree(importer))

    treeImporterUsed(defnDef, importer) shouldBe false
  }

  test("apply() for the name of a Term.Param should not try to match it and return false") {
    val importer = importer"a.foo"

    treeImporterUsed(param"foo: x.X", importer) shouldBe false

    verifyNoInteractions(termNameImporterMatcher)
  }

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
