package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{DefnValClassifier, JavaStatClassifier}
import io.github.effiban.scala2java.core.contexts.{CtorContext, StatContext, TemplateChildContext}
import io.github.effiban.scala2java.core.matchers.CtorContextMatcher.eqCtorContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.ArgumentMatchers

import scala.meta.{Ctor, Defn, Init, Lit, Name, Pat, Term, Type}

class TemplateChildTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val CtorContextWithClassName = CtorContext(
    javaScope = JavaScope.Class,
    className = ClassName,
    inits = TheInits)

  private val ChildContextWithClassName = TemplateChildContext(
    javaScope = JavaScope.Class,
    maybeClassName = Some(ClassName),
    inits = TheInits)

  private val PrimaryCtorArgs = List(
    termParam("arg1", "Int"),
    termParam("arg2", "String")
  )
  private val SecondaryCtorArgs = List(
    termParam("arg3", "Int"),
    termParam("arg4", "String")
  )

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )

  private val SecondaryCtor = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = Init(tpe = Type.Singleton(Term.This(Name.Anonymous())), name = Name.Anonymous(), argss = List(Nil)),
    stats = Nil
  )

  private val TheDefnVal = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(name = Term.Name("y"))),
    decltpe = None,
    rhs = Lit.Int(4)
  )

  private val TheDefnDef = Defn.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(List(termParam("param", "Int"))),
    decltpe = Some(TypeNames.Int),
    body = Term.Apply(Term.Name("foo"), Nil)
  )

  private val ctorPrimaryTraverser = mock[CtorPrimaryTraverser]
  private val ctorSecondaryTraverser = mock[CtorSecondaryTraverser]
  private val enumConstantListTraverser = mock[EnumConstantListTraverser]
  private val statTraverser = mock[StatTraverser]
  private val defnValClassifier = mock[DefnValClassifier]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val templateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    enumConstantListTraverser,
    statTraverser,
    defnValClassifier,
    javaStatClassifier
  )

  test("traverse() for primary ctor. when class name provided") {
    doWrite(
      """{
        |   /* PRIMARY CTOR */
        |}""".stripMargin)
      .when(ctorPrimaryTraverser).traverse(primaryCtor = eqTree(PrimaryCtor), ctorContext = eqCtorContext(CtorContextWithClassName))

    templateChildTraverser.traverse(child = PrimaryCtor, context = ChildContextWithClassName)

    outputWriter.toString shouldBe
      """{
        |   /* PRIMARY CTOR */
        |}""".stripMargin
  }

  test("traverse() for primary ctor. when class name not provided should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = PrimaryCtor, context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }

  test("traverse() for secondary ctor. when class name provided") {
    doWrite(
      """{
        |   /* SECONDARY CTOR */
        |}""".stripMargin)
      .when(ctorSecondaryTraverser).traverse(secondaryCtor = eqTree(SecondaryCtor), ctorContext = eqCtorContext(CtorContextWithClassName))

    templateChildTraverser.traverse(child = SecondaryCtor, context = ChildContextWithClassName)

    outputWriter.toString shouldBe
      """{
        |   /* SECONDARY CTOR */
        |}""".stripMargin
  }

  test("traverse() for secondary ctor. without ctor. context should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = SecondaryCtor, context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }

  test("traverse() for Defn.Val which is not an enum constant list, and requires end delimiter") {

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVal), ArgumentMatchers.eq(JavaScope.Class))).thenReturn(false)
    doWrite("/* DATA MEMBER DEFINITION */")
      .when(statTraverser).traverse(eqTree(TheDefnVal), ArgumentMatchers.eq(StatContext(JavaScope.Class)))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnVal))).thenReturn(true)

    templateChildTraverser.traverse(child = TheDefnVal, context = TemplateChildContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """/* DATA MEMBER DEFINITION */;
        |""".stripMargin
  }

  test("traverse() for Defn.Val which is an enum constant list") {

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVal), ArgumentMatchers.eq(JavaScope.Class))).thenReturn(true)
    doWrite("/* ENUM CONSTANTS */".stripMargin).when(enumConstantListTraverser).traverse(eqTree(TheDefnVal))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnVal))).thenReturn(true)

    templateChildTraverser.traverse(child = TheDefnVal, context = TemplateChildContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """/* ENUM CONSTANTS */;
        |""".stripMargin
  }

  test("traverse() for stat which does not require end delimiter") {

    doWrite(
      """{
        |    /* METHOD DEFINITION */
        |}""".stripMargin)
      .when(statTraverser).traverse(eqTree(TheDefnDef), ArgumentMatchers.eq(StatContext(JavaScope.Class)))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnDef))).thenReturn(false)

    templateChildTraverser.traverse(child = TheDefnDef, context = TemplateChildContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """{
        |    /* METHOD DEFINITION */
        |}""".stripMargin
  }

  test("traverse() for non-stat should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = Name("blabla"), context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
