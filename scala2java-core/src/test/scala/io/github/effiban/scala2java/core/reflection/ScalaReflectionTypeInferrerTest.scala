package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.entities.TermNames.Scala
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTypeInferrer.inferScalaMetaTypeOf
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ScalaReflectionTypeInferrerTest extends UnitTestSuite {

  private val TestClass = t"io.github.effiban.scala2java.core.reflection.TestClass"
  private val TestInnerClass = t"io.github.effiban.scala2java.core.reflection.TestClass#TestInnerClass"
  private val TestParameterizedClass = t"io.github.effiban.scala2java.core.reflection.TestParameterizedClass"
  private val TestChildParameterizedClass = t"io.github.effiban.scala2java.core.reflection.TestChildParameterizedClass"
  private val TestChildParameterizedClass2 = t"io.github.effiban.scala2java.core.reflection.TestChildParameterizedClass2"
  private val TestObject = q"io.github.effiban.scala2java.core.reflection.TestObject"
  private val TestObjectType = Type.Singleton(TestObject)
  private val TestAliases = q"io.github.effiban.scala2java.core.reflection.TestAliases"


  private val NoQualifierTypeScenarios = Table(
    ("Qualifier", "Name", "Expected type"),
    (q"scala.collection.immutable", q"Nil", t"scala.collection.immutable.List[scala.Nothing]"),
    (Scala, q"None", t"scala.Option[scala.Nothing]"),
    (q"io.github.effiban.scala2java.core.reflection", q"TestObject", TestObjectType),
    (TestObject, q"x", t"scala.Int"),
    (TestAliases, q"TestObject", TestObjectType),
  )

  private val QualifierTypeWithoutArgsScenarios = Table(
    ("Qualifier Type", "Name", "Expected type"),
    (TestClass, q"x", t"scala.Int"),
    (TestInnerClass, q"x", t"scala.Int"),
    (TestClass, q"y", t"(scala.Int, java.lang.String)"),
    (TestClass, q"z", t"(scala.Int, scala.Long, java.lang.String) => java.lang.String"),
    (TestClass, q"w", t"scala.collection.immutable.List[scala.Long]"),
    (TestObjectType, q"x", t"scala.Int")
  )

  private val QualifierTypeWithArgsScenarios = Table(
    ("Qualifier Type", "Qualifier Type Args", "Name", "Expected type"),
    (TestParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"x", t"scala.Int"),
    (TestParameterizedClass, List(t"AA", t"BB", t"CC"), q"x", t"AA"),
    (TestParameterizedClass, List(t"scala.collection.immutable.List[AA]", t"BB", t"CC"), q"x",
      t"scala.collection.immutable.List[AA]"),
    (TestParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"y", t"(scala.Int, scala.Long)"),
    (TestParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"z",
      t"(scala.Int, scala.Long, java.lang.String) => java.lang.String"),
    (TestParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"w",
      t"scala.collection.immutable.List[scala.Int]"),
    (TestParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"v",
      t"scala.collection.immutable.List[scala.collection.immutable.List[scala.Int]]"),
    (TestParameterizedClass, List(t"AA", t"BB", t"CC"), q"v",
      t"scala.collection.immutable.List[scala.collection.immutable.List[AA]]"),
    (TestChildParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"x", t"scala.Int"),
    (TestChildParameterizedClass, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"y", t"(scala.Int, scala.Long)"),
    (TestChildParameterizedClass2, List(t"scala.Int", t"scala.Long", t"java.lang.String"), q"x",
      t"scala.collection.immutable.List[scala.Int]"),
    (TestChildParameterizedClass2, List(t"AA", t"BB", t"CC"), q"x", t"scala.collection.immutable.List[AA]")
  )

  forAll(NoQualifierTypeScenarios) { (qual: Term.Ref, name: Term.Name, expectedType: Type) =>
    test(s"inferScalaMetaTypeOf() for '$qual' and '$name' should return type '$expectedType'") {
      inferScalaMetaTypeOf(qual, name).value.structure shouldBe expectedType.structure
    }
  }

  forAll(QualifierTypeWithoutArgsScenarios) { (qualType: Type.Ref, name: Term.Name, expectedType: Type) =>
    test(s"inferScalaMetaTypeOf() for type '$qualType' and '$name' should return type '$expectedType'") {
      inferScalaMetaTypeOf(qualType, name).value.structure shouldBe expectedType.structure
    }
  }

  forAll(QualifierTypeWithArgsScenarios) { (qualType: Type.Ref, qualArgs: List[Type], name: Term.Name, expectedType: Type) =>
    test(s"inferScalaMetaTypeOf() for type '$qualType' with args '$qualArgs' and '$name', should return type '$expectedType'") {
      inferScalaMetaTypeOf(qualType, qualArgs, name).value.structure shouldBe expectedType.structure
    }
  }
}
