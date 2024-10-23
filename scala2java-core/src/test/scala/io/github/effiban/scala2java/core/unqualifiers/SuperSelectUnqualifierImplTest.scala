package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.binders.FileScopeNonInheritedTermNameBinder
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.InheritedTermNameOwnersInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.collection.MapView
import scala.collection.immutable.ListMap
import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class SuperSelectUnqualifierImplTest extends UnitTestSuite {

  private val inheritedTermNameOwnersInferrer = mock[InheritedTermNameOwnersInferrer]
  private val fileScopeNonInheritedTermNameBinder = mock[FileScopeNonInheritedTermNameBinder]

  private val superSelectUnqualifier = new SuperSelectUnqualifierImpl(inheritedTermNameOwnersInferrer, fileScopeNonInheritedTermNameBinder)

  test("unqualify() when has more than one enclosing class which inherits the term, " +
    "and also clashes with file-scope declarations, " +
    "should return a Term.Select with the inputs unchanged except 'superp' empty") {

    val termSuper = q"A.super[A1]"
    val termName = q"c"
    val expectedTermSelect = q"A.super.c"

    val classA = q"class A"
    val classB = q"class B"

    val typeA1 = t"A1"
    val typeA2 = t"A2"
    val typeB1 = t"B1"
    val typeB2 = t"B2"

    doReturn(
      ListMap(
        classA.templ -> List(typeA1, typeA2),
        classB.templ -> List(typeB1, typeB2)
      )).when(inheritedTermNameOwnersInferrer).infer(eqTree(termName), eqQualificationContext(QualificationContext()))

    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(termName))).thenReturn(Some(q"val c: Int"))

    superSelectUnqualifier.unqualify(termSuper, termName, None).structure shouldBe expectedTermSelect.structure
  }

  test("unqualify() when has more than one enclosing class which inherits the term, " +
    "and does not clash with file-scope declarations, " +
    "should return a Term.Select with the inputs unchanged except 'superp' empty") {

    val termSuper = q"A.super[A1]"
    val termName = q"c"
    val expectedTermSelect = q"A.super.c"

    val classA = q"class A"
    val classB = q"class B"

    val typeA1 = t"A1"
    val typeA2 = t"A2"
    val typeB1 = t"B1"
    val typeB2 = t"B2"

    doReturn(
      ListMap(
        classA.templ -> List(typeA1, typeA2),
        classB.templ -> List(typeB1, typeB2)
      )).when(inheritedTermNameOwnersInferrer).infer(eqTree(termName), eqQualificationContext(QualificationContext()))

    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(termName))).thenReturn(None)

    superSelectUnqualifier.unqualify(termSuper, termName, None).structure shouldBe expectedTermSelect.structure
  }

  test("unqualify() when has exactly one enclosing class which inherits the term, " +
    "but clashes with a declaration in the same file, " +
    "should return a Term.Select with a default 'super' and same term name") {

    val termSuper = q"A.super[A1]"
    val termName = q"c"
    val expectedTermSelect = q"super.c"

    val classA = q"class A"

    val typeA1 = t"A1"
    val typeA2 = t"A2"

    doReturn(
      ListMap(
        classA.templ -> List(typeA1, typeA2)
      )).when(inheritedTermNameOwnersInferrer).infer(eqTree(termName), eqQualificationContext(QualificationContext()))

    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(termName))).thenReturn(Some(q"val c: Int"))

    superSelectUnqualifier.unqualify(termSuper, termName, None).structure shouldBe expectedTermSelect.structure
  }

  test("unqualify() when has exactly one enclosing class which inherits the term, " +
    "and does not clash with a declaration in the same file, " +
    "and IS typed, " +
    "should return a Term.Select with a default 'super' and same term name") {

    val termSuper = q"A.super[A1]"
    val termName = q"c"
    val termSelectParent = q"A.super[A1].c[T]"
    val expectedTermSelect = q"super.c"

    val classA = q"class A"

    val typeA1 = t"A1"
    val typeA2 = t"A2"

    doReturn(ListMap(
      classA.templ -> List(typeA1, typeA2)
    )).when(inheritedTermNameOwnersInferrer).infer(eqTree(termName), eqQualificationContext(QualificationContext()))

    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(termName))).thenReturn(None)

    superSelectUnqualifier.unqualify(termSuper, termName, Some(termSelectParent)).structure shouldBe expectedTermSelect.structure
  }

  test("unqualify() when has exactly one enclosing class which inherits the term, " +
    "and does not clash with a declaration in the same file, " +
    "and is NOT typed, " +
    "should return the term name only") {

    val termSuper = q"A.super[A1]"
    val termName = q"c"
    val termSelectParent = q"A.super[A1].c"

    val classA = q"class A"

    val typeA1 = t"A1"
    val typeA2 = t"A2"

    doReturn(ListMap(
      classA.templ -> List(typeA1, typeA2)
    )).when(inheritedTermNameOwnersInferrer).infer(eqTree(termName), eqQualificationContext(QualificationContext()))

    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(termName))).thenReturn(None)

    superSelectUnqualifier.unqualify(termSuper, termName, Some(termSelectParent)).structure shouldBe termName.structure
  }
}
