package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier.Public
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaScope, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames

import scala.meta.{Decl, Mod, Name, Pat, Term}

class JavaAllExtraModifiersResolverImplTest extends UnitTestSuite {

  private val Context = ModifiersContext(
    scalaTree = Decl.Val(List(Mod.Private(Name.Anonymous())), List(Pat.Var(Term.Name("x"))), TypeNames.Int),
    javaTreeType = JavaTreeType.Variable,
    javaScope = JavaScope.Class
  )

  private val singleResolver1 = mock[JavaExtraModifierResolver]
  private val singleResolver2 = mock[JavaExtraModifierResolver]
  private val singleResolver3 = mock[JavaExtraModifierResolver]

  private val allResolver = new JavaAllExtraModifiersResolverImpl(
    List(singleResolver1, singleResolver2, singleResolver3)
  )

  private val Scenarios = Table(
    ("MaybeJavaModifier1", "MaybeJavaModifier2", "MaybeJavaModifier3", "ExpectedModifiers"),
    (Some(Public), None, None, Set(JavaModifier.Public)),
    (None, Some(JavaModifier.Public), None, Set(JavaModifier.Public)),
    (Some(JavaModifier.Public), Some(JavaModifier.Final), None, Set(JavaModifier.Public, JavaModifier.Final)),
    (None, None, None, Set.empty),
  )

  forAll(Scenarios) { case (maybeModifier1, maybeModifier2, maybeModifier3, expectedModifiers) =>
    test(s"When the single resolvers return: ($maybeModifier1, $maybeModifier2, $maybeModifier3) the modifiers should be: $expectedModifiers") {
      when(singleResolver1.resolve(eqModifiersContext(Context))).thenReturn(maybeModifier1)
      when(singleResolver2.resolve(eqModifiersContext(Context))).thenReturn(maybeModifier2)
      when(singleResolver3.resolve(eqModifiersContext(Context))).thenReturn(maybeModifier3)

      allResolver.resolve(Context) shouldBe expectedModifiers
    }
  }
}
