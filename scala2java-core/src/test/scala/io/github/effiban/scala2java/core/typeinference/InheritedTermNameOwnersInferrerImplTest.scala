package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.collection.immutable.ListMap
import scala.meta.{Defn, Pat, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InheritedTermNameOwnersInferrerImplTest extends UnitTestSuite {

  private val enclosingTemplateAncestorsInferrer = mock[EnclosingTemplateAncestorsInferrer]
  private val templateAncestorsInferrer = mock[TemplateAncestorsInferrer]

  private val inheritedTermNameOwnersInferrer = new InheritedTermNameOwnersInferrerImpl(
    enclosingTemplateAncestorsInferrer,
    templateAncestorsInferrer
  )

  test("infer() when the template has no ancestors, should return empty") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        class NoParents {
          val x: Int = 3
        }
      }
      """

    val cls = pkg.stats.head.asInstanceOf[Defn.Class]
    val templ = cls.templ
    val termName = cls.templ.stats.head.asInstanceOf[Defn.Val]
      .pats.head.asInstanceOf[Pat.Var]
      .name

    val context = QualificationContext()

    doReturn(Nil).when(templateAncestorsInferrer).infer(eqTree(templ), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.infer(termName, cls.templ, context) shouldBe empty
  }

  test("infer() when the template has ancestors, but the Term.Name is not a member of any parent - should return empty") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        import io.github.effiban.scala2java.core.typeinference.Parent1

        private class Child4 extends Parent1 {
          val z: Int = 3
        }
      }
      """

    val cls = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val templ = cls.templ
    val termName = cls.templ.stats.head.asInstanceOf[Defn.Val]
      .pats.head.asInstanceOf[Pat.Var]
      .name

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"io.github.effiban.scala2java.core.typeinference.Parent1"))

    doReturn(
      List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      )
    ).when(templateAncestorsInferrer).infer(eqTree(templ), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.infer(termName, templ, context) shouldBe empty
  }

  test("infer() when the template has ancestors, and the Term.Name is a member of some, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        import io.github.effiban.scala2java.core.typeinference.Parent1

        private class Child1 extends Parent1 {
          x
        }
      }
      """

    val cls = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val templ = cls.templ
    val termName = templ.stats.head.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"io.github.effiban.scala2java.core.typeinference.Parent1"))

    doReturn(
      List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      )
    ).when(templateAncestorsInferrer).infer(eqTree(templ), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.infer(termName, templ, context).structure shouldBe
      List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      ).structure
  }

  test("inferAll() when Term.Name has one enclosing member without ancestors, should return empty") {
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

    val context = QualificationContext()

    doReturn(ListMap.empty).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.inferAll(termName, context) shouldBe empty
  }

  test("inferAll() when Term.Name has one enclosing member with ancestors, but it's not a member of any parent, should return empty") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        import io.github.effiban.scala2java.core.typeinference.Parent1

        private class Child4 extends Parent1 {
          val z: Int = 3
        }
      }
      """

    val cls = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val termName = cls.templ.stats.head.asInstanceOf[Defn.Val]
      .pats.head.asInstanceOf[Pat.Var]
      .name

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"io.github.effiban.scala2java.core.typeinference.Parent1"))

    doReturn(ListMap(cls.templ ->
      List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      )
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.inferAll(termName, context) shouldBe empty
  }

  test("inferAll() when Term.Name has one enclosing member with ancestors, and it's a member of some, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        import io.github.effiban.scala2java.core.typeinference.Parent1

        private class Child1 extends Parent1 {
          x
        }
      }
      """

    val cls = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val termName = cls.templ.stats.head.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"io.github.effiban.scala2java.core.typeinference.Parent1"))

    doReturn(ListMap(cls.templ ->
      List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      )
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    val resultMap = inheritedTermNameOwnersInferrer.inferAll(termName, context)
    resultMap.size shouldBe 1
    val (resultTemplate, resultParents) = resultMap.head
    resultTemplate.structure shouldBe cls.templ.structure
    resultParents.structure shouldBe List(
      t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
    ).structure
  }

  test("inferAll() when Term.Name has two enclosing members with ancestors, and it's a member of one, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {

        import io.github.effiban.scala2java.core.typeinference.Parent1
        import io.github.effiban.scala2java.core.typeinference.Parent2

        private class Child1 extends Parent1 {
          x

          private class Child3 extends Parent2 {
            y
          }
        }
      }
      """

    val child1 = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val child3 = child1.templ.stats.collectFirst { case cls: Defn.Class => cls }.get
    val y = child3.templ.stats.head.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"Parent1" -> t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"Parent2" -> t"io.github.effiban.scala2java.core.typeinference.Parent2"
    ))

    doReturn(ListMap(
      child3.templ -> List(
        t"io.github.effiban.scala2java.core.typeinference.Parent2",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent2",
      ),
      child1.templ -> List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      ),
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(y), eqQualificationContext(context))

    val resultMap = inheritedTermNameOwnersInferrer.inferAll(y, context)
    resultMap.size shouldBe 1
    val (resultMember, resultParents) = resultMap.head
    resultMember.structure shouldBe child1.templ.structure
    resultParents.structure shouldBe List(
      t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
    ).structure
  }

  test("inferAll() when Term.Name has two enclosing members with ancestors, and it's a member of both, should return a corresponding Map") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {

        import io.github.effiban.scala2java.core.typeinference.Parent1
        import io.github.effiban.scala2java.core.typeinference.Parent2

        private class Child1 extends Parent1 {
          x

          private class Child2 extends Parent2 {
            x
          }
        }
      }
      """

    val child1 = pkg.stats.collectFirst { case cls: Defn.Class => cls }.get
    val child2 = child1.templ.stats.collectFirst { case cls: Defn.Class => cls }.get
    val innerX = child2.templ.stats.head.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"Parent1" -> t"io.github.effiban.scala2java.core.typeinference.Parent1",
      t"Parent2" -> t"io.github.effiban.scala2java.core.typeinference.Parent2"
    ))

    doReturn(ListMap(
      child2.templ -> List(
        t"io.github.effiban.scala2java.core.typeinference.Parent2",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent2",
      ),
      child1.templ -> List(
        t"io.github.effiban.scala2java.core.typeinference.Parent1",
        t"io.github.effiban.scala2java.core.typeinference.Grandparent1"
      )
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(innerX), eqQualificationContext(context))

    val resultMap = inheritedTermNameOwnersInferrer.inferAll(innerX, context)
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

