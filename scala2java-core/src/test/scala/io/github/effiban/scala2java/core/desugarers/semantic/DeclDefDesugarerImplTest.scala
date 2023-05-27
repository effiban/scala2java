package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class DeclDefDesugarerImplTest extends UnitTestSuite {

  private val termParamDesugarer = mock[TermParamDesugarer]

  private val declDefDesugarer = new DeclDefDesugarerImpl(termParamDesugarer)


  test("desugar") {
    val declDef = q"def foo(x: Int = func, y: String = func2)(z: Int = func3): Unit"

    val desugaredDeclDef = q"def foo(x: Int = func(), y: String = func2())(z: Int = func3()): Unit"

    doAnswer((termParam: Term.Param) => termParam.default match {
      case Some(q"func") => termParam.copy(default = Some(q"func()"))
      case Some(q"func2") => termParam.copy(default = Some(q"func2()"))
      case Some(q"func3") => termParam.copy(default = Some(q"func3()"))
    }).when(termParamDesugarer).desugar(any[Term.Param])

    declDefDesugarer.desugar(declDef).structure shouldBe desugaredDeclDef.structure
  }

}
