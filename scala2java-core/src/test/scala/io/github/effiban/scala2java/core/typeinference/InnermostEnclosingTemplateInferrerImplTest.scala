package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.InnermostEnclosingTemplateInferrer.infer

import scala.meta.Term.Block
import scala.meta.{Defn, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class InnermostEnclosingTemplateInferrerImplTest extends UnitTestSuite {

  test("infer for inner class with enclosing member name, should return the corresponding outer class template") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            class E
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val classE = classD.templ.stats.head

    infer(classE, Some("C")).value.structure shouldBe classC.templ.structure
  }

  test("infer for inner class without enclosing member name, should return the innermost outer class template") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            class E
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val classE = classD.templ.stats.head

    infer(classE).value.structure shouldBe classD.templ.structure
  }

  test("infer for Defn.Val of class with enclosing member name, should return the template of the corresponding enclosing class") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            val e: scala.Int = 3
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val e = classD.templ.stats.head

    infer(e, Some("C")).value.structure shouldBe classC.templ.structure
  }

  test("infer for Defn.Val of class without enclosing member name, should return the template of the innermost enclosing class") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            val e: scala.Int = 3
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val e = classD.templ.stats.head

    infer(e).value.structure shouldBe classD.templ.structure
  }

  test("infer for Defn.Val of template without a parent class, should return the template") {
    val template =
      template"""
      A {
        val e: scala.Int = 3
      }
      """

    val e = template.stats.head

    infer(e).value.structure shouldBe template.structure
  }

  test("infer for Defn.Val inside a Defn.Def with enclosing member name, should return the corresponding class template") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            def e(): scala.Int = {
              val f: scala.Int = 3
              f
            }
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val defnDefE = classD.templ.stats.head.asInstanceOf[Defn.Def]
    val f = defnDefE.body.asInstanceOf[Block].stats.head

    infer(f, Some("C")).value.structure shouldBe classC.templ.structure
  }

  test("infer for Defn.Val inside a Defn.Def without enclosing member name, should return the innermost class template") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D {
            def e(): scala.Int = {
              val f: scala.Int = 3
              f
            }
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head.asInstanceOf[Defn.Class]
    val defnDefE = classD.templ.stats.head.asInstanceOf[Defn.Def]
    val f = defnDefE.body.asInstanceOf[Block].stats.head

    infer(f).value.structure shouldBe classD.templ.structure
  }
}
