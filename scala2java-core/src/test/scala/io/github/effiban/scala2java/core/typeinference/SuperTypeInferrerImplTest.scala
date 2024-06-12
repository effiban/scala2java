package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.SuperTypeInferrer.infer

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class SuperTypeInferrerImplTest extends UnitTestSuite {

  test("infer when there is one direct parent") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        class SuperTypeInferrerImplTest {
          class MyClass1 extends scala.collection.immutable.LinearSeq[Int] {
            val x = super[LinearSeq].length
          }
        }
      }
      """

    val theSuper = pkg.collect {case aSuper: Term.Super => aSuper}.head

    SuperTypeInferrer.infer(theSuper).value.structure shouldBe t"scala.collection.immutable.LinearSeq".structure

  }

  test("infer when there are two direct parents") {
    val pkg =
      q"""
      package io.github.effiban.scala2java.core.typeinference {
        class SuperTypeInferrerImplTest {
          class MyClass2 extends scala.collection.immutable.LinearSeq[Int] with scala.collection.immutable.Iterable[Int] {
            val x = super[Iterable].length
          }
        }
      }
      """

    val theSuper = pkg.collect {case aSuper: Term.Super => aSuper}.head

    infer(theSuper).value.structure shouldBe t"scala.collection.immutable.Iterable".structure
  }

  private class MyClass1 extends scala.collection.immutable.LinearSeq[Int] {
    val x = super[LinearSeq].length
  }

  private class MyClass2 extends scala.collection.immutable.LinearSeq[Int] with scala.collection.immutable.Iterable[Int] {
    val x = super[Iterable].last
  }
}

