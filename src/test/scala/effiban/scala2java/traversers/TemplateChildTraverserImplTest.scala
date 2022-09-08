package effiban.scala2java.traversers

import effiban.scala2java.classifiers.JavaStatClassifier
import effiban.scala2java.entities.CtorContext
import effiban.scala2java.matchers.CtorContextMatcher.eqCtorContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Ctor, Defn, Init, Lit, Name, Pat, Term, Type}

class TemplateChildTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheCtorContext = CtorContext(className = ClassName, inits = TheInits)

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
  private val statTraverser = mock[StatTraverser]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val templateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    statTraverser,
    javaStatClassifier
  )

  test("traverse() for primary ctor. with context") {
    doWrite(
      """{
        |   /* PRIMARY CTOR */
        |}""".stripMargin)
      .when(ctorPrimaryTraverser).traverse(primaryCtor = eqTree(PrimaryCtor), ctorContext = eqCtorContext(TheCtorContext))

    templateChildTraverser.traverse(child = PrimaryCtor, maybeCtorContext = Some(TheCtorContext))

    outputWriter.toString shouldBe
      """{
        |   /* PRIMARY CTOR */
        |}""".stripMargin
  }

  test("traverse() for primary ctor. without context should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = PrimaryCtor)
    }
  }

  test("traverse() for secondary ctor. with context") {
    doWrite(
      """{
        |   /* SECONDARY CTOR */
        |}""".stripMargin)
      .when(ctorSecondaryTraverser).traverse(secondaryCtor = eqTree(SecondaryCtor), ctorContext = eqCtorContext(TheCtorContext))

    templateChildTraverser.traverse(child = SecondaryCtor, maybeCtorContext = Some(TheCtorContext))

    outputWriter.toString shouldBe
      """{
        |   /* SECONDARY CTOR */
        |}""".stripMargin
  }

  test("traverse() for secondary ctor. without context should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = SecondaryCtor)
    }
  }

  test("traverse() for stat requiring end delimiter") {

    doWrite("/* DATA MEMBER DEFINITION */").when(statTraverser).traverse(eqTree(TheDefnVal))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnVal))).thenReturn(true)

    templateChildTraverser.traverse(child = TheDefnVal)

    outputWriter.toString shouldBe
      """/* DATA MEMBER DEFINITION */;
        |""".stripMargin
  }

  test("traverse() for stat which does not require end delimiter") {

    doWrite(
      """{
        |    /* METHOD DEFINITION */
        |}""".stripMargin)
      .when(statTraverser).traverse(eqTree(TheDefnDef))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnVal))).thenReturn(false)

    templateChildTraverser.traverse(child = TheDefnDef)

    outputWriter.toString shouldBe
      """{
        |    /* METHOD DEFINITION */
        |}""".stripMargin
  }

  test("traverse() for non-stat should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = Name("blabla"))
    }
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
