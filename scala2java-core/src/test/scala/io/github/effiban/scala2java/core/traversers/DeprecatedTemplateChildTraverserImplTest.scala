package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{DefnVarClassifier, JavaStatClassifier, TraitClassifier}
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.matchers.CtorContextMatcher.eqCtorContext
import io.github.effiban.scala2java.core.renderers.contexts.{CtorSecondaryRenderContext, DefRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.CtorSecondaryRenderContextMatcher.eqCtorSecondaryRenderContext
import io.github.effiban.scala2java.core.renderers.{CtorSecondaryRenderer, DefnDefRenderer, EnumConstantListRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.{CtorSecondaryTraversalResult, DefnDefTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Init, Lit, Name, Pat, Term, Type, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

@deprecated
class DeprecatedTemplateChildTraverserImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"
  private val TraversedClassName = t"MyTraversedClass"
  private val TermClassName = q"MyClass"

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"
  private val TheScalaMods = List(TheAnnot)
  private val TheTraversedScalaMods = List(TheTraversedAnnot)

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

  private val PrimaryCtorDefnDef = Defn.Def(
    mods = Nil,
    name = TermClassName,
    tparams = Nil,
    paramss = List(PrimaryCtorArgs),
    decltpe = Some(Type.AnonymousName()),
    body = Term.Apply(Term.Name("foo"), Nil)
  )

  private val SecondaryCtor = Ctor.Secondary(
    mods = TheScalaMods,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = Init(tpe = Type.Singleton(Term.This(Name.Anonymous())), name = Name.Anonymous(), argss = List(Nil)),
    stats = List(q"foo(1)", q"bar(2)")
  )

  private val TraversedSecondaryCtor = Ctor.Secondary(
    mods = TheTraversedScalaMods,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = Init(tpe = Type.Singleton(Term.This(Name.Anonymous())), name = Name.Anonymous(), argss = List(Nil)),
    stats = List(q"foo(11)", q"bar(22)")
  )

  private val TheDefnVar = Defn.Var(
    mods = Nil,
    pats = List(Pat.Var(name = Term.Name("y"))),
    decltpe = None,
    rhs = Some(Lit.Int(4))
  )

  private val TheDefnDef = Defn.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(List(termParam("param", "Int"))),
    decltpe = Some(TypeNames.Int),
    body = Term.Apply(Term.Name("foo"), Nil)
  )
  
  private val TheTrait = q"trait MyTrait { def foo(): Unit = doSomething() } "

  private val ctorPrimaryTraverser = mock[CtorPrimaryTraverser]
  private val defnDefRenderer = mock[DefnDefRenderer]
  private val ctorSecondaryTraverser = mock[CtorSecondaryTraverser]
  private val ctorSecondaryRenderer = mock[CtorSecondaryRenderer]
  private val enumConstantListRenderer = mock[EnumConstantListRenderer]
  private val statTraverser = mock[DeprecatedStatTraverser]
  private val defnValClassifier = mock[DefnVarClassifier]
  private val traitClassifier = mock[TraitClassifier]
  private val javaStatClassifier = mock[JavaStatClassifier]

  private val templateChildTraverser = new DeprecatedTemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    defnDefRenderer,
    ctorSecondaryTraverser,
    ctorSecondaryRenderer,
    enumConstantListRenderer,
    statTraverser,
    defnValClassifier,
    traitClassifier,
    javaStatClassifier
  )

  test("traverse() for primary ctor. when class name provided") {
    val ctorJavaModifiers: List[JavaModifier] = List(JavaModifier.Public)
    val traversalResult = DefnDefTraversalResult(PrimaryCtorDefnDef, ctorJavaModifiers)

    doReturn(traversalResult)
      .when(ctorPrimaryTraverser).traverse(primaryCtor = eqTree(PrimaryCtor), ctorContext = eqCtorContext(CtorContextWithClassName))

    doWrite(
      """{
        |   /* PRIMARY CTOR */
        |}""".stripMargin)
      .when(defnDefRenderer).render(eqTree(PrimaryCtorDefnDef), eqTo(DefRenderContext(ctorJavaModifiers)))

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
    val ctorJavaModifiers = List(JavaModifier.Public)
    val traversalResult = CtorSecondaryTraversalResult(
      tree = TraversedSecondaryCtor,
      className = TraversedClassName,
      javaModifiers = ctorJavaModifiers
    )
    doReturn(traversalResult)
      .when(ctorSecondaryTraverser).traverse(eqTree(SecondaryCtor), eqCtorContext(CtorContextWithClassName))

    val expectedRenderContext = CtorSecondaryRenderContext(TraversedClassName, ctorJavaModifiers)

    doWrite(
      """{
        |   /* SECONDARY CTOR */
        |}""".stripMargin)
      .when(ctorSecondaryRenderer).render(eqTree(TraversedSecondaryCtor), eqCtorSecondaryRenderContext(expectedRenderContext))

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

  test("traverse() for Defn.Var which is not an enum constant list, and requires end delimiter") {

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVar), eqTo(JavaScope.Class))).thenReturn(false)
    doWrite("/* DATA MEMBER DEFINITION */")
      .when(statTraverser).traverse(eqTree(TheDefnVar), eqTo(StatContext(JavaScope.Class)))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnVar))).thenReturn(true)

    templateChildTraverser.traverse(child = TheDefnVar, context = TemplateChildContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """/* DATA MEMBER DEFINITION */;
        |""".stripMargin
  }

  test("traverse() for Defn.Var which is an enum constant list") {

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVar), eqTo(JavaScope.Class))).thenReturn(true)
    doWrite("/* ENUM CONSTANTS */".stripMargin).when(enumConstantListRenderer).render(eqTree(TheDefnVar))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheDefnVar))).thenReturn(true)

    templateChildTraverser.traverse(child = TheDefnVar, context = TemplateChildContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """/* ENUM CONSTANTS */;
        |""".stripMargin
  }

  test("traverse() for a Trait which is not an enum constant list") {
    when(traitClassifier.isEnumTypeDef(eqTree(TheTrait), eqTo(JavaScope.Class))).thenReturn(false)
    doWrite("/* TRAIT DEFINITION */")
      .when(statTraverser).traverse(eqTree(TheTrait), eqTo(StatContext(JavaScope.Class)))
    when(javaStatClassifier.requiresEndDelimiter(eqTree(TheTrait))).thenReturn(false)

    templateChildTraverser.traverse(child = TheTrait, context = TemplateChildContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe "/* TRAIT DEFINITION */"
  }

  test("traverse() for a Trait which is an enum constant list, should skip it") {
    when(traitClassifier.isEnumTypeDef(eqTree(TheTrait), eqTo(JavaScope.Enum))).thenReturn(true)

    templateChildTraverser.traverse(child = TheTrait, context = TemplateChildContext(javaScope = JavaScope.Enum))

    outputWriter.toString shouldBe ""
  }

  test("traverse() for stat which does not require end delimiter") {

    doWrite(
      """{
        |    /* METHOD DEFINITION */
        |}""".stripMargin)
      .when(statTraverser).traverse(eqTree(TheDefnDef), eqTo(StatContext(JavaScope.Class)))
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