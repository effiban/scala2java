package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{DefnVarClassifier, TraitClassifier}
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.matchers.CtorContextMatcher.eqCtorContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.core.traversers.results.matchers.StatTraversalResultScalatestMatcher.equalStatTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Init, Name, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class TemplateChildTraverserImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"
  private val TraversedClassName = t"MyTraversedClass"
  private val TermClassName = q"MyClass"

  private val TheInits = List(init"Parent1()", init"Parent2()")

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

  private val PrimaryCtorArgs = List(param"arg1: Int", param"arg2: Int")

  private val SecondaryCtorArgs = List(param"arg3: Int", param"arg4: Int")

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

  private val TheDefnVar = q"var y = 4"
  private val TheTraversedDefnVar = q"var yy = 44"

  private val TheDefnDef = q"def myMethod(param: Int) = foo(param)"
  private val TheTraversedDefnDef = q"def myTraversedMethod(param: Int) = traversedFoo(param)"

  private val TheTrait = q"trait MyTrait { def foo(): Unit = doSomething() } "

  private val ctorPrimaryTraverser = mock[CtorPrimaryTraverser]
  private val ctorSecondaryTraverser = mock[CtorSecondaryTraverser]
  private val defaultStatTraverser = mock[DefaultStatTraverser]
  private val defnValClassifier = mock[DefnVarClassifier]
  private val traitClassifier = mock[TraitClassifier]

  private val templateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    defaultStatTraverser,
    defnValClassifier,
    traitClassifier
  )

  test("traverse() for primary ctor. when class name provided") {
    val ctorJavaModifiers: List[JavaModifier] = List(JavaModifier.Public)
    val traversalResult = DefnDefTraversalResult(PrimaryCtorDefnDef, ctorJavaModifiers)

    doReturn(traversalResult)
      .when(ctorPrimaryTraverser).traverse(primaryCtor = eqTree(PrimaryCtor), ctorContext = eqCtorContext(CtorContextWithClassName))

    val actualTraversalResult = templateChildTraverser.traverse(child = PrimaryCtor, context = ChildContextWithClassName)
    actualTraversalResult should equalStatTraversalResult(traversalResult)
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

    val actualTraversalResult = templateChildTraverser.traverse(child = SecondaryCtor, context = ChildContextWithClassName)
    actualTraversalResult should equalStatTraversalResult(traversalResult)
  }

  test("traverse() for secondary ctor. without ctor. context should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = SecondaryCtor, context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }

  test("traverse() for Defn.Var which is not an enum constant list, and requires end delimiter") {
    val traversalResult = DefnVarTraversalResult(TheTraversedDefnVar)

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVar), eqTo(JavaScope.Class))).thenReturn(false)
    doReturn(traversalResult)
      .when(defaultStatTraverser).traverse(eqTree(TheDefnVar), eqTo(StatContext(JavaScope.Class)))

    val actualTraversalResult = templateChildTraverser.traverse(
      child = TheDefnVar,
      context = TemplateChildContext(javaScope = JavaScope.Class)
    )
    actualTraversalResult should equalStatTraversalResult(traversalResult)
  }

  test("traverse() for Defn.Var which is an enum constant list") {
    val traversalResult = EnumConstantListTraversalResult(TheDefnVar)

    when(defnValClassifier.isEnumConstantList(eqTree(TheDefnVar), eqTo(JavaScope.Class))).thenReturn(true)

    val actualResult = templateChildTraverser.traverse(child = TheDefnVar, context = TemplateChildContext(javaScope = JavaScope.Class))
    actualResult should equalStatTraversalResult(traversalResult)
  }

  test("traverse() for a Trait which is not an enum type def") {
    // TODO - once the TraitTraversalResult class is available
  }

  test("traverse() for a Trait which is an enum type def, should skip it") {
    when(traitClassifier.isEnumTypeDef(eqTree(TheTrait), eqTo(JavaScope.Enum))).thenReturn(true)

    val actualResult = templateChildTraverser.traverse(child = TheTrait, context = TemplateChildContext(javaScope = JavaScope.Enum))
    actualResult shouldBe EmptyStatTraversalResult
  }

  test("traverse() for a Defn.Def") {
    val traversalResult = DefnDefTraversalResult(TheTraversedDefnDef)

    doReturn(traversalResult)
      .when(defaultStatTraverser).traverse(eqTree(TheDefnDef), eqTo(StatContext(JavaScope.Class)))

    val actualResult = templateChildTraverser.traverse(child = TheDefnDef, context = TemplateChildContext(javaScope = JavaScope.Class))
    actualResult should equalStatTraversalResult(traversalResult)
  }

  test("traverse() for non-stat should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = Name("blabla"), context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }
}
