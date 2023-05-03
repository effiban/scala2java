package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Mod, Type, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TypeRendererImplTest extends UnitTestSuite {

  private val typeApplyInfixRenderer = mock[TypeApplyInfixRenderer]
  private val typeLambdaRenderer = mock[TypeLambdaRenderer]
  private val typeAnonymousParamRenderer = mock[TypeAnonymousParamRenderer]
  private val typeVarRenderer = mock[TypeVarRenderer]

  private val typeRenderer = new TypeRendererImpl(
    typeApplyInfixRenderer,
    typeLambdaRenderer,
    typeAnonymousParamRenderer,
    typeVarRenderer
  )

  test("render Type.ApplyInfix") {
    val typeApplyInfix = t"K Map V"

    typeRenderer.render(typeApplyInfix)

    verify(typeApplyInfixRenderer).render(eqTree(typeApplyInfix))
  }


  test("render Type.Lambda") {
    val typeLambda = Type.Lambda(tparams = List(tparam"T1", tparam"T2"), tpe = t"U")
    typeRenderer.render(typeLambda)
    verify(typeLambdaRenderer).render(eqTree(typeLambda))
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
