package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ApplyTypeTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val typeTraverser = mock[TypeTraverser]

  private val applyTypeTraverser = new ApplyTypeTraverserImpl(expressionTermTraverser, typeTraverser)

  test("traverse") {
    val fun = q"myFunc"
    val type1 = t"T1"
    val type2 = t"T2"
    val applyType = Term.ApplyType(fun, List(type1, type2))

    val traversedFun = q"myTraversedFunc"
    val traversedType1 = t"U1"
    val traversedType2 = t"U2"
    val traversedApplyType = Term.ApplyType(traversedFun, List(traversedType1, traversedType2))

    doReturn(traversedFun).when(expressionTermTraverser).traverse(eqTree(applyType.fun))
    doAnswer((tpe: Type) => tpe match {
      case aType if aType.structure == type1.structure => traversedType1
      case aType if aType.structure == type2.structure => traversedType2
      case aType => aType
    }).when(typeTraverser).traverse(any[Type])

    applyTypeTraverser.traverse(applyType).structure shouldBe traversedApplyType.structure
  }

}
