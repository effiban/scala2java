package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Mod, Name, Term, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(MethodSignature)

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()), Mod.Final())

  private val TheParamName = q"myParam"

  private val TheType = t"Type1"
  private val TheTraversedType = t"Type2"

  private val TheDefault = q"3"
  private val TheTraversedDefault = q"33"

  private val termParamModListTraverser = mock[TermParamModListTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termParamTraverser = new TermParamTraverserImpl(
    termParamModListTraverser,
    typeTraverser,
    expressionTermTraverser
  )

  test("traverse with type and default") {
    val termParam = Term.Param(
      mods = TheScalaMods,
      name = TheParamName,
      decltpe = Some(TheType),
      default = Some(TheDefault)
    )
    val traversedTermParam = Term.Param(
      mods = TheTraversedScalaMods,
      name = TheParamName,
      decltpe = Some(TheTraversedType),
      default = Some(TheTraversedDefault)
    )

    doReturn(TheTraversedScalaMods).when(termParamModListTraverser).traverse(eqTree(termParam), eqTo(MethodSignature))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doReturn(TheTraversedDefault).when(expressionTermTraverser).traverse(eqTree(TheDefault))

    termParamTraverser.traverse(termParam, TheStatContext).structure shouldBe traversedTermParam.structure
  }

  test("traverse without type and without default") {
    val termParam = Term.Param(
      mods = TheScalaMods,
      name = TheParamName,
      decltpe = None,
      default = None
    )
    val traversedTermParam = Term.Param(
      mods = TheTraversedScalaMods,
      name = TheParamName,
      decltpe = None,
      default = None
    )

    doReturn(TheTraversedScalaMods).when(termParamModListTraverser).traverse(eqTree(termParam), eqTo(MethodSignature))

    termParamTraverser.traverse(termParam, TheStatContext).structure shouldBe traversedTermParam.structure
  }
}
