package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Mod, Name}

class JavaAllExtraModifiersResolverImplTest extends UnitTestSuite {

  private val Context = JavaModifiersContext(
    scalaTree = Lit.Int(3),
    scalaMods = List(Mod.Private(Name.Anonymous())),
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
    (Some(JavaModifier.Public), None, None, Set(JavaModifier.Public)),
    (None, Some(JavaModifier.Public), None, Set(JavaModifier.Public)),
    (Some(JavaModifier.Public), Some(JavaModifier.Final), None, Set(JavaModifier.Public, JavaModifier.Final)),
    (None, None, None, Set.empty),
  )

  forAll(Scenarios) { case (maybeModifier1, maybeModifier2, maybeModifier3, expectedModifiers) =>
    test(s"When the single resolvers return: ($maybeModifier1, $maybeModifier2, $maybeModifier3) the modifiers should be: $expectedModifiers") {
      when(singleResolver1.resolve(eqJavaModifiersContext(Context))).thenReturn(maybeModifier1)
      when(singleResolver2.resolve(eqJavaModifiersContext(Context))).thenReturn(maybeModifier2)
      when(singleResolver3.resolve(eqJavaModifiersContext(Context))).thenReturn(maybeModifier3)

      allResolver.resolve(Context) shouldBe expectedModifiers
    }
  }
}
