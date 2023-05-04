package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Mod, Type, XtensionQuasiquoteType}

class TypeRendererImplTest extends UnitTestSuite {

  private val typeRefRenderer = mock[TypeRefRenderer]
  private val typeApplyRenderer = mock[TypeApplyRenderer]
  private val typeApplyInfixRenderer = mock[TypeApplyInfixRenderer]
  private val typeWithRenderer = mock[TypeWithRenderer]
  private val typeRefineRenderer = mock[TypeRefineRenderer]
  private val typeExistentialRenderer = mock[TypeExistentialRenderer]
  private val typeAnnotateRenderer = mock[TypeAnnotateRenderer]
  private val typeAnonymousParamRenderer = mock[TypeAnonymousParamRenderer]
  private val typeVarRenderer = mock[TypeVarRenderer]

  private val typeRenderer = new TypeRendererImpl(
    typeRefRenderer,
    typeApplyRenderer,
    typeApplyInfixRenderer,
    typeWithRenderer,
    typeRefineRenderer,
    typeExistentialRenderer,
    typeAnnotateRenderer,
    typeAnonymousParamRenderer,
    typeVarRenderer
  )

  test("render Type.Select") {
    val typeSelect = t"a.b.C"

    typeRenderer.render(typeSelect)

    verify(typeRefRenderer).render(eqTree(typeSelect))
  }

  test("render Type.Apply") {
    val typeApply = t"Map[K, V]"

    typeRenderer.render(typeApply)

    verify(typeApplyRenderer).render(eqTree(typeApply))
  }

  test("render Type.ApplyInfix") {
    val typeApplyInfix = t"K Map V"

    typeRenderer.render(typeApplyInfix)

    verify(typeApplyInfixRenderer).render(eqTree(typeApplyInfix))
  }

  test("render Type.With") {
    val typeWith = t"A with B"
    typeRenderer.render(typeWith)
    verify(typeWithRenderer).render(eqTree(typeWith))
  }

  test("render Type.Refine") {
    val typeRefine = t"A { def foo(): Int }"
    typeRenderer.render(typeRefine)
    verify(typeRefineRenderer).render(eqTree(typeRefine))
  }

  test("render Type.Existential") {
    val typeExistential = t"A forSome { type B }"
    typeRenderer.render(typeExistential)
    verify(typeExistentialRenderer).render(eqTree(typeExistential))
  }

  test("render Type.Annotate") {
    val typeAnnotate = t"A @annot"
    typeRenderer.render(typeAnnotate)
    verify(typeAnnotateRenderer).render(eqTree(typeAnnotate))
  }

  test("render Type.AnonymousParam") {
    val typeAnonymousParam = Type.AnonymousParam(Some(Mod.Contravariant()))
    typeRenderer.render(typeAnonymousParam)
    verify(typeAnonymousParamRenderer).render(eqTree(typeAnonymousParam))
  }


  test("render Type.Var") {
    val typeVar = Type.Var(Type.Name("x"))
    typeRenderer.render(typeVar)
    verify(typeVarRenderer).render(eqTree(typeVar))
  }
}
