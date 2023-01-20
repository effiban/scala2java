package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateChildContext}
import io.github.effiban.scala2java.core.factories.TemplateChildContextFactory.create
import io.github.effiban.scala2java.core.matchers.TemplateChildContextScalatestMatcher.equalTemplateChildContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateChildContextFactoryTest extends UnitTestSuite {

  private val TheClassName = t"MyClass"
  private val ThePrimaryCtor = q"def this(param1: Int, param2: String)"
  private val TheInits = List(init"MyParent1()", init"MyParent2()")
  private val TheTerms = List(q"term1", q"term2")

  test("create() when has primary ctor.") {
    val bodyContext = TemplateBodyContext(
      JavaScope.Class,
      Some(TheClassName),
      Some(ThePrimaryCtor),
      TheInits)

    val expectedChildContext = TemplateChildContext(
      JavaScope.Class,
      Some(TheClassName),
      TheInits,
      TheTerms
    )

    create(bodyContext, TheTerms) should equalTemplateChildContext(expectedChildContext)
  }

  test("create() when has no primary ctor.") {
    val bodyContext = TemplateBodyContext(
      JavaScope.Class,
      Some(TheClassName),
      None,
      TheInits)

    val expectedChildContext = TemplateChildContext(
      JavaScope.Class,
      Some(TheClassName),
      TheInits,
      Nil
    )

    create(bodyContext, TheTerms) should equalTemplateChildContext(expectedChildContext)
  }
}
