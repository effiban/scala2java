package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Defn, Pat, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InheritedTermNameOwnersInferrerImplTest extends UnitTestSuite {

  private val enclosingTemplateAncestorsInferrer = mock[EnclosingTemplateAncestorsInferrer]

  private val inheritedTermNameOwnersInferrer = new InheritedTermNameOwnersInferrerImpl(enclosingTemplateAncestorsInferrer)

  test("infer() when Term.Name has one enclosing member without parents, should return empty") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        class NoParents {
          val x: Int = 3
        }
      }
      """

    val cls = pkg.stats.head.asInstanceOf[Defn.Class]
    val termName = cls.templ.stats.head.asInstanceOf[Defn.Val]
      .pats.head.asInstanceOf[Pat.Var]
      .name

    when(enclosingTemplateAncestorsInferrer.infer(eqTree(termName))).thenReturn(Map.empty)

    inheritedTermNameOwnersInferrer.infer(termName) shouldBe empty
  }

  test("infer() when Term.Name has one enclosing member with parents, but it's not a member of any parent, should return empty") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        private class Child4 extends io.github.effiban.scala2java.core.typeinference.Parent1 {
          val z: Int = 3
        }
      }
      """

    val cls = pkg.stats.head.asInstanceOf[Defn.Class]
    val termName = cls.templ.stats.head.asInstanceOf[Defn.Val]
      .pats.head.asInstanceOf[Pat.Var]
      .name

    when(enclosingTemplateAncestorsInferrer.infer(eqTree(termName))).thenReturn(
      Map(cls.templ ->
        List(
          t"io.github.effiban.scala2java.core.typeinference.Parent1",
          t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
        )
      )
    )

    inheritedTermNameOwnersInferrer.infer(termName) shouldBe empty
  }

  test("infer() when Term.Name has one enclosing member with parents, and it's a member of some, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        private class Child1 extends io.github.effiban.scala2java.core.typeinference.Parent1 {
          x
        }
      }
      """

    val cls = pkg.stats.head.asInstanceOf[Defn.Class]
    val termName = cls.templ.stats.head.asInstanceOf[Term.Name]

    when(enclosingTemplateAncestorsInferrer.infer(eqTree(termName))).thenReturn(
      Map(cls.templ ->
        List(
          t"io.github.effiban.scala2java.core.typeinference.Parent1",
          t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
       )
      )
    )

    val resultMap = inheritedTermNameOwnersInferrer.infer(termName)
    resultMap.size shouldBe 1
    val (resultTemplate, resultParents) = resultMap.head
    resultTemplate.structure shouldBe cls.templ.structure
    resultParents.structure shouldBe List(
      t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
    ).structure
  }

  test("infer() when Term.Name has two enclosing members with parents, and it's a member of one, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {

        private class Child1 extends io.github.effiban.scala2java.core.typeinference.Parent1 {
          x

          private class Child3 extends io.github.effiban.scala2java.core.typeinference.Parent2 {
            y
          }
        }
      }
      """

    val child1 = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val child3 = child1.templ.stats.collectFirst { case cls: Defn.Class => cls }.get
    val y = child3.templ.stats.head.asInstanceOf[Term.Name]

    when(enclosingTemplateAncestorsInferrer.infer(eqTree(y))).thenReturn(
      Map(
        child1.templ -> List(
          t"io.github.effiban.scala2java.core.typeinference.Parent1",
          t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
        ),
        child3.templ -> List(
          t"io.github.effiban.scala2java.core.typeinference.Parent2",
          t"io.github.effiban.scala2java.core.typeinference.Grandparent2",
        ),
      )
    )

    val resultMap = inheritedTermNameOwnersInferrer.infer(y)
    resultMap.size shouldBe 1
    val (resultMember, resultParents) = resultMap.head
    resultMember.structure shouldBe child1.templ.structure
    resultParents.structure shouldBe List(
      t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
    ).structure
  }

  test("infer() when Term.Name has two enclosing members with parents, and it's a member of both, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {

        private class Child1 extends io.github.effiban.scala2java.core.typeinference.Parent1 {
          x

          private class Child2 extends io.github.effiban.scala2java.core.typeinference.Parent2 {
            x
          }
        }
      }
      """

    val child1 = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val child2 = child1.templ.stats.collectFirst { case cls: Defn.Class => cls }.get
    val innerX = child2.templ.stats.head.asInstanceOf[Term.Name]

    when(enclosingTemplateAncestorsInferrer.infer(eqTree(innerX)))
      .thenReturn(
        Map(
          child1.templ -> List(
            t"io.github.effiban.scala2java.core.typeinference.Parent1",
            t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
          ),
          child2.templ -> List(
            t"io.github.effiban.scala2java.core.typeinference.Parent2",
            t"io.github.effiban.scala2java.core.typeinference.Grandparent2",
          )
        )
      )

    val resultMap = inheritedTermNameOwnersInferrer.infer(innerX)
    resultMap.size shouldBe 2

    val inferredChild1Parents = TreeKeyedMap(resultMap, child1.templ)
    inferredChild1Parents.structure shouldBe List(
      t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
    ).structure

    val inferredChild2Parents = TreeKeyedMap(resultMap, child2.templ)
    inferredChild2Parents.structure shouldBe List(
      t"io.github.effiban.scala2java.core.typeinference.Parent2",
      t"io.github.effiban.scala2java.core.typeinference.Grandparent2"
    ).structure
  }
}

private trait GreatGrandparent1
private trait GreatGrandparent2

private trait Grandparent1 extends GreatGrandparent1 {
  val x: Int = 1
  val y: Int = 3
}

private trait Grandparent2 extends GreatGrandparent2 {
  val x: Int = 2
}

private trait Parent1 extends Grandparent1 {
  override val x: Int = 11
  override val y: Int = 33
}

private trait Parent2 extends Grandparent2 {
  override val x: Int = 22
}

private class Child1 extends Parent1 {
  x

  private class Child2 extends Parent2 {
    x
  }

  private class Child3 extends Parent2 {
    y
  }
}

private class Child4 extends Parent1 {
  val z: Int = 3
}

private class NoParents {
  val x: Int = 3
}

