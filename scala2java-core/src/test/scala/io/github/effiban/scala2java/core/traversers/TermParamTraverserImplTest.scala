package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.WithJavaModifiersTraversalResultScalatestMatcher.equalWithJavaModifiersTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{ModListTraversalResult, TermParamTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Mod, Name, Term, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermParamTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.MethodSignature)

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot, Mod.Private(Name.Anonymous()))
  private val TheTraversedScalaMods = List(TheTraversedAnnot, Mod.Private(Name.Anonymous()))
  private val TheJavaModifiers = List(JavaModifier.Private, JavaModifier.Final)
  private val TheModListTraversalResult = ModListTraversalResult(TheTraversedScalaMods, TheJavaModifiers)

  private val TheParamName = q"myParam"
  private val TheTraversedParamName = q"traversedMyParam"

  private val TheType = t"Type1"
  private val TheTraversedType = t"Type2"

  private val TheDefault = q"3"
  private val TheTraversedDefault = q"33"

  private val statModListTraverser = mock[StatModListTraverser]
  private val nameTraverser = mock[NameTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termParamTraverser = new TermParamTraverserImpl(
    statModListTraverser,
    nameTraverser,
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
      name = TheTraversedParamName,
      decltpe = Some(TheTraversedType),
      default = Some(TheTraversedDefault)
    )

    val expectedResult = TermParamTraversalResult(traversedTermParam, TheJavaModifiers)

    doReturn(TheModListTraversalResult).when(statModListTraverser).traverse(eqExpectedModifiers(termParam))
    doReturn(TheTraversedParamName).when(nameTraverser).traverse(eqTree(TheParamName))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doReturn(TheTraversedDefault).when(expressionTermTraverser).traverse(eqTree(TheDefault))

    termParamTraverser.traverse(termParam, TheStatContext) should equalWithJavaModifiersTraversalResult(expectedResult)
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
      name = TheTraversedParamName,
      decltpe = None,
      default = None
    )

    val expectedResult = TermParamTraversalResult(traversedTermParam, TheJavaModifiers)

    doReturn(TheModListTraversalResult).when(statModListTraverser).traverse(eqExpectedModifiers(termParam))
    doReturn(TheTraversedParamName).when(nameTraverser).traverse(eqTree(TheParamName))

    termParamTraverser.traverse(termParam, TheStatContext)

    termParamTraverser.traverse(termParam, TheStatContext) should equalWithJavaModifiersTraversalResult(expectedResult)
  }

  private def eqExpectedModifiers(termParam: Term.Param) = {
    val expectedModifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, JavaScope.MethodSignature)
    eqModifiersContext(expectedModifiersContext)
  }
}
