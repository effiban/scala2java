package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.InnermostEnclosingMemberPathInferrer.infer

import scala.meta.Term.Block
import scala.meta.{Defn, XtensionQuasiquoteTerm}

class InnermostEnclosingMemberPathInferrerTest extends UnitTestSuite {

  test("infer for class of top-level package without given name, should return the package") {
    val pkg =
      q"""
      package a.b {
        class C
      }
      """

    val clsInPkg = pkg.stats.head

    infer(clsInPkg).structure shouldBe List(pkg).structure
  }

  test("infer for class of top-level package with given name, should return the package") {
    val pkg =
      q"""
      package a.b {
        class C
      }
      """

    val clsInPkg = pkg.stats.head

    infer(clsInPkg, Some("b")).structure shouldBe List(pkg).structure
  }

  test("infer for class of top-level package with incorrect name, should return empty") {
    val pkg =
      q"""
      package a.b {
        class C
      }
      """

    val clsInPkg = pkg.stats.head

    infer(clsInPkg, Some("zzz")) shouldBe Nil
  }

  test("infer for trait of top-level package without given name, should return the package") {
    val pkg =
      q"""
      package a.b {
        trait C
      }
      """

    val traitInPkg = pkg.stats.head

    infer(traitInPkg).structure shouldBe List(pkg).structure
  }

  test("infer for object of top-level package without given name, should return the package") {
    val pkg =
      q"""
      package a.b {
        object C
      }
      """

    val objectInPkg = pkg.stats.head

    infer(objectInPkg).structure shouldBe List(pkg).structure
  }

  test("infer for nested class of top-level package with package name, should return the package") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D
        }
      }
      """

    val nestedClass = pkg.stats.head.asInstanceOf[Defn.Class].templ.stats.head

    infer(nestedClass, Some("b")).structure shouldBe List(pkg).structure
  }

  test("infer for nested class of top-level package without given name, should return the path to the enclosing class") {
    val pkg =
      q"""
      package a.b {
        class C {
          class D
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val classD = classC.templ.stats.head

    infer(classD).structure shouldBe List(pkg, classC).structure
  }

  test("infer for Defn.Val of class without given name, should return the path to the class") {
    val pkg =
      q"""
      package a.b {
        class C {
          val d: scala.Int = 3
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val d = classC.templ.stats.head

    infer(d).structure shouldBe List(pkg, classC).structure
  }

  test("infer for Defn.Val inside a Defn.Def of a class without given name, should return the path to the class") {
    val pkg =
      q"""
      package a.b {
        class C {
          def d(): scala.Int = {
            val e: scala.Int = 3
            e
          }
        }
      }
      """

    val classC = pkg.stats.head.asInstanceOf[Defn.Class]
    val d = classC.templ.stats.head.asInstanceOf[Defn.Def]
    val e = d.body.asInstanceOf[Block].stats.head

    infer(e).structure shouldBe List(pkg, classC).structure
  }
}
