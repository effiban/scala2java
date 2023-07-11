package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnVarTypeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]

  private val defnVarTypeTraverser = new DefnVarTypeTraverserImpl(
    typeTraverser,
    termTypeInferrer
  )

  test("traverse when has declared type should traverse it") {
    val tpe = t"MyType"
    val traversedType = t"MyTraversedType"

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    val maybeOutputType = defnVarTypeTraverser.traverse(maybeDeclType = Some(tpe))
    maybeOutputType.value.structure shouldBe traversedType.structure
  }

  test("traverse when has no declared type, has RHS, and type is inferred - should traverse it") {
    val rhs = q"myInstance"
    val tpe = t"MyType"
    val traversedType = t"MyTraversedType"

    when(termTypeInferrer.infer(eqTree(rhs))).thenReturn(Some(tpe))
    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    val maybeOutputType = defnVarTypeTraverser.traverse(maybeRhs = Some(rhs))
    maybeOutputType.value.structure shouldBe traversedType.structure
  }

  test("traverse when has no declared type, has RHS, and type not inferred - should return None") {
    val rhs = q"nonInferrable"

    when(termTypeInferrer.infer(eqTree(rhs))).thenReturn(None)

    defnVarTypeTraverser.traverse(maybeRhs = Some(rhs)) shouldBe None
  }

  test("traverse when has no declared type and has no RHS - should return None") {
    defnVarTypeTraverser.traverse() shouldBe None
  }
}
