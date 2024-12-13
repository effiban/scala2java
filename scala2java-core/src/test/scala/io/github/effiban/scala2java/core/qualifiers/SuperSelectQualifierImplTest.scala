package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermSupers
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.collection.MapView
import scala.collection.immutable.ListMap
import scala.meta.{Defn, Name, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class SuperSelectQualifierImplTest extends UnitTestSuite {

  private val innermostEnclosingTemplateInferrer = mock[InnermostEnclosingTemplateInferrer]
  private val inheritedTermNameOwnersInferrer = mock[InheritedTermNameOwnersInferrer]

  private val superSelectQualifier = new SuperSelectQualifierImpl(innermostEnclosingTemplateInferrer, inheritedTermNameOwnersInferrer)

  test("qualify when has a thisp, has a superp, and has an enclosing template - should return unchanged") {

    val clsA =
      q"""
      class A extends B {
        def b(): Int = A.super[B].c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(Some(templA))

    superSelectQualifier.qualify(theSuper, c, QualificationContext()).structure shouldBe selectSuperC.structure
  }

  test("qualify when has a thisp, has a superp, and no enclosing template - should return unchanged") {

    val selectSuperC = q"A.super[B].c"
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(None)
    doReturn(MapView.empty).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(c), eqQualificationContext(QualificationContext()))

    superSelectQualifier.qualify(theSuper, c, QualificationContext()).structure shouldBe selectSuperC.structure
  }

  test("qualify when has a thisp, has no superp, has an enclosing template, and one parent found - " +
    "should return the input with a superp for the parent") {

    val clsA =
      q"""
      class A extends B {
        def b(): Int = A.super.c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext(qualifiedTypeMap = Map(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(Some(templA))
    doReturn(List(t"qualB.B")).when(inheritedTermNameOwnersInferrer).infer(eqTree(c), eqTree(templA), eqQualificationContext(context))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe q"A.super[B].c".structure
  }

  test("qualify when has a thisp, has no superp, has an enclosing template, and two parents found - " +
    "should return the input with a superp for the first parent") {

    val clsA =
      q"""
      class A extends B with C {
        def b(): Int = A.super.c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"B" -> t"qualB.B",
      t"C" -> t"qualC.C"
    ))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(Some(templA))
    doReturn(List(t"qualB.B")).when(inheritedTermNameOwnersInferrer).infer(eqTree(c), eqTree(templA), eqQualificationContext(context))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe q"A.super[B].c".structure
  }

  test("qualify when has a thisp, has no superp, has an enclosing template, but no parents found - should return unchanged") {
    val clsA =
      q"""
      class A extends B {
        def b(): Int = A.super.c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext(qualifiedTypeMap = Map(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(Some(templA))
    doReturn(List.empty).when(inheritedTermNameOwnersInferrer).infer(eqTree(c), eqTree(templA), eqQualificationContext(context))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe selectSuperC.structure
  }

  test("qualify when has a thisp, has no superp, and has no enclosing template and no inherited owners - should return unchanged") {
    val selectSuperC = q"A.super.c"
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext()

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(Some("A")))).thenReturn(None)
    doReturn(ListMap.empty).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(c), eqQualificationContext(context))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe selectSuperC.structure
  }

  test("qualify when has no thisp, has a superp, and has an enclosing template - should return the input with a thisp for the member") {
    val clsA =
      q"""
      class A extends B {
        def b(): Int = super[B].c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext()

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Some(templA))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe q"A.super[B].c".structure
  }

  test("qualify when has no thisp, has a superp, and no enclosing template - should return unchanged") {

    val termSelect = Term.Select(Term.Super(Name.Anonymous(), Name.Indeterminate("A")), q"x")
    val termSuper = termSelect.qual.asInstanceOf[Term.Super]

    val context = QualificationContext()

    when(innermostEnclosingTemplateInferrer.infer(eqTree(termSuper), eqTo(None))).thenReturn(None)

    superSelectQualifier.qualify(termSuper, termSelect.name, context).structure shouldBe termSelect.structure
  }

  test("qualify when has no thisp, has no superp, has an enclosing template, and parent found - " +
    "should return input with a Super with a thisp of enclosing member and superp for the parent") {

    val clsA =
      q"""
      class A extends B {
        def b(): Int = super.c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext(qualifiedTypeMap = Map(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Some(templA))
    doReturn(List(t"qualB.B"))
      .when(inheritedTermNameOwnersInferrer).infer(eqTree(c), eqTree(templA), eqQualificationContext(context))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe q"A.super[B].c".structure
  }

  test("qualify when has no thisp, has no superp, has an enclosing template, and no parent found - " +
    "should return input with a Super having a thisp of enclosing member and no superp") {

    val clsA =
      q"""
      class A extends B {
        def b(): Int = super.c
      }
      """

    val templA = clsA.templ
    val b = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val selectSuperC = b.body.asInstanceOf[Term.Select]
    val theSuper = selectSuperC.qual.asInstanceOf[Term.Super]
    val c = selectSuperC.name

    val context = QualificationContext(qualifiedTypeMap = Map(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Some(templA))
    doReturn(List.empty).when(inheritedTermNameOwnersInferrer).infer(eqTree(c), eqTree(templA), eqQualificationContext(context))

    superSelectQualifier.qualify(theSuper, c, context).structure shouldBe q"A.super.c".structure
  }

  test("qualify when has no thisp and no superp, and no enclosing template - should return unchanged") {
    val termSelect = Term.Select(TermSupers.Empty, q"x")
    val termName = termSelect.name

    val context = QualificationContext(qualifiedTypeMap = Map(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(TermSupers.Empty), eqTo(None))).thenReturn(None)
    doReturn(ListMap.empty).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(termName), eqQualificationContext(context))

    superSelectQualifier.qualify(TermSupers.Empty, termName, context).structure shouldBe termSelect.structure
  }
}
