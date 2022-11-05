package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.ObjectClassifier.isStandalone
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{Selfs, Templates}

import scala.meta.{Defn, Init, Name, Template, Term, Type}

class ObjectClassifierTest extends UnitTestSuite {

  test("isStandalone when has inits should return false") {
    val inits = List(
      Init(tpe = Type.Name("A"), name = Name.Anonymous(), argss = List(Nil)),
      Init(tpe = Type.Name("B"), name = Name.Anonymous(), argss = List(Nil))
    )

    val objectDef = Defn.Object(Nil, Term.Name("C"), Template(Nil, inits, Selfs.Empty, Nil))

    isStandalone(objectDef) shouldBe false
  }

  test("isStandalone when has no inits should return true") {
    val objectDef = Defn.Object(Nil, Term.Name("C"), Templates.Empty)

    isStandalone(objectDef) shouldBe true
  }
}
