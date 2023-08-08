package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.TraitClassifier
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.matchers.CtorContextMatcher.eqCtorContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Init, Name, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionStructure}

class TemplateChildTraverserImplTest extends UnitTestSuite {

  private val ClassName = t"MyClass"
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
  private val TheTraversedTrait = q"trait MyTraversedTrait { def tFoo(): Unit = doSomething() } "

  private val ctorPrimaryTraverser = mock[CtorPrimaryTraverser]
  private val ctorSecondaryTraverser = mock[CtorSecondaryTraverser]
  private val defaultStatTraverser = mock[DefaultStatTraverser]
  private val traitClassifier = mock[TraitClassifier]

  private val templateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    defaultStatTraverser,
    traitClassifier
  )

  test("traverse() for primary ctor. when class name provided") {
    doReturn(PrimaryCtorDefnDef)
      .when(ctorPrimaryTraverser).traverse(primaryCtor = eqTree(PrimaryCtor), ctorContext = eqCtorContext(CtorContextWithClassName))

    val maybeTraversedTemplateChild = templateChildTraverser.traverse(child = PrimaryCtor, context = ChildContextWithClassName)
    maybeTraversedTemplateChild.value.structure shouldBe PrimaryCtorDefnDef.structure
  }

  test("traverse() for primary ctor. when class name not provided should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = PrimaryCtor, context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }

  test("traverse() for secondary ctor.") {
    val childContext = TemplateChildContext(javaScope = JavaScope.Class)

    doReturn(TraversedSecondaryCtor).when(ctorSecondaryTraverser).traverse(eqTree(SecondaryCtor))

    val maybeTraversedTemplateChild = templateChildTraverser.traverse(child = SecondaryCtor, context = childContext)
    maybeTraversedTemplateChild.value.structure shouldBe TraversedSecondaryCtor.structure 
  }

  test("traverse() for a Trait which is not an enum type def") {
    when(traitClassifier.isEnumTypeDef(eqTree(TheTrait), eqTo(JavaScope.Enum))).thenReturn(false)
    doReturn(Some(TheTraversedTrait))
      .when(defaultStatTraverser).traverse(eqTree(TheTrait), eqTo(StatContext(JavaScope.Class)))

    val maybeTraversedTemplateChild = templateChildTraverser.traverse(
      child = TheTrait,
      context = TemplateChildContext(javaScope = JavaScope.Class)
    )
    maybeTraversedTemplateChild.value.structure shouldBe TheTraversedTrait.structure
  }

  test("traverse() for a Trait which is an enum type def, should skip it") {
    when(traitClassifier.isEnumTypeDef(eqTree(TheTrait), eqTo(JavaScope.Enum))).thenReturn(true)

    val maybeTraversedTemplateChild = templateChildTraverser.traverse(child = TheTrait, context = TemplateChildContext(javaScope = JavaScope.Enum))
    maybeTraversedTemplateChild shouldBe None
  }

  test("traverse() for a Defn.Def") {
    doReturn(Some(TheTraversedDefnDef))
      .when(defaultStatTraverser).traverse(eqTree(TheDefnDef), eqTo(StatContext(JavaScope.Class)))

    val maybeTraversedTemplateChild = templateChildTraverser.traverse(child = TheDefnDef, context = TemplateChildContext(javaScope = JavaScope.Class))
    maybeTraversedTemplateChild.value.structure shouldBe TheTraversedDefnDef.structure
  }

  test("traverse() for Defn.Var") {
    doReturn(Some(TheTraversedDefnVar))
      .when(defaultStatTraverser).traverse(eqTree(TheDefnVar), eqTo(StatContext(JavaScope.Class)))

    val maybeTraversedTemplateChild = templateChildTraverser.traverse(
      child = TheDefnVar,
      context = TemplateChildContext(javaScope = JavaScope.Class)
    )
    maybeTraversedTemplateChild.value.structure shouldBe TheTraversedDefnVar.structure
  }

  test("traverse() for non-stat should throw exception") {
    intercept[IllegalStateException] {
      templateChildTraverser.traverse(child = Name("blabla"), context = TemplateChildContext(javaScope = JavaScope.Class))
    }
  }
}
