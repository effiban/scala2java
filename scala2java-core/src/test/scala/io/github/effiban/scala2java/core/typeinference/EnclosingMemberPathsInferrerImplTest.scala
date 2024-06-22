package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Defn, XtensionQuasiquoteTerm}

class EnclosingMemberPathsInferrerImplTest extends UnitTestSuite {

  private val innermostEnclosingMemberPathInferrer = mock[InnermostEnclosingMemberPathInferrer]

  private val enclosingMemberPathsInferrer = new EnclosingMemberPathsInferrerImpl(innermostEnclosingMemberPathInferrer)

  test("") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            val e: Int = 3
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val defnValE = classD.templ.stats.head

    when(innermostEnclosingMemberPathInferrer.infer(eqTree(defnValE), eqTo(None)))
      .thenReturn(List(pkg, classC, classD))

    enclosingMemberPathsInferrer.infer(defnValE).structure shouldBe List(
      List(pkg),
      List(pkg, classC),
      List(pkg, classC, classD)
    ).structure
  }
}
