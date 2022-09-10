package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.matchers.JavaModifiersContextMatcher.eqJavaModifiersContext
import effiban.scala2java.predicates.JavaRequiresFinalModifierPredicate
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Mod, Name}

class JavaSupplementalModifiersResolverImplTest extends UnitTestSuite {

  private val javaPublicModifierResolver = mock[JavaPublicModifierResolver]
  private val javaRequiresFinalModifierPredicate = mock[JavaRequiresFinalModifierPredicate]

  private val javaSupplementalModifiersResolver = new JavaSupplementalModifiersResolverImpl(
    javaPublicModifierResolver,
    javaRequiresFinalModifierPredicate
  )

  test("resolve() when scala mods include 'private' and 'final' not required should return empty") {
    val context = JavaModifiersContext(
      scalaTree = Lit.Int(3),
      scalaMods = List(Mod.Private(Name.Anonymous())),
      javaTreeType = JavaTreeType.Variable,
      javaScope = JavaTreeType.Class
    )

    when(javaRequiresFinalModifierPredicate.apply(eqJavaModifiersContext(context))).thenReturn(false)

    javaSupplementalModifiersResolver.resolve(context) shouldBe Set.empty

    verifyZeroInteractions(javaPublicModifierResolver)
  }

  test("resolve() when scala mods include 'protected' and 'final' not required should return empty") {
    val context = JavaModifiersContext(
      scalaTree = Lit.Int(3),
      scalaMods = List(Mod.Protected(Name.Anonymous())),
      javaTreeType = JavaTreeType.Variable,
      javaScope = JavaTreeType.Class
    )

    when(javaRequiresFinalModifierPredicate.apply(eqJavaModifiersContext(context))).thenReturn(false)

    javaSupplementalModifiersResolver.resolve(context) shouldBe Set.empty

    verifyZeroInteractions(javaPublicModifierResolver)
  }

  test("resolve() when scala mods are empty and 'final' not required should return ['public']") {
    val context = JavaModifiersContext(
      scalaTree = Lit.Int(3),
      scalaMods = Nil,
      javaTreeType = JavaTreeType.Variable,
      javaScope = JavaTreeType.Class
    )

    when(javaPublicModifierResolver.resolve(eqJavaModifiersContext(context))).thenReturn(Some(JavaModifier.Public))
    when(javaRequiresFinalModifierPredicate.apply(eqJavaModifiersContext(context))).thenReturn(false)

    javaSupplementalModifiersResolver.resolve(context) shouldBe Set(JavaModifier.Public)
  }

  test("resolve() when scala mods include 'private' and 'final' is required should return ['final']") {
    val context = JavaModifiersContext(
      scalaTree = Lit.Int(3),
      scalaMods = List(Mod.Private(Name.Anonymous())),
      javaTreeType = JavaTreeType.Variable,
      javaScope = JavaTreeType.Class
    )

    when(javaRequiresFinalModifierPredicate.apply(eqJavaModifiersContext(context))).thenReturn(true)

    javaSupplementalModifiersResolver.resolve(context) shouldBe Set(JavaModifier.Final)

    verifyZeroInteractions(javaPublicModifierResolver)
  }

  test("resolve() when scala mods are empty and 'final' is required should return ['public', 'final']") {
    val context = JavaModifiersContext(
      scalaTree = Lit.Int(3),
      scalaMods = Nil,
      javaTreeType = JavaTreeType.Variable,
      javaScope = JavaTreeType.Class
    )

    when(javaPublicModifierResolver.resolve(eqJavaModifiersContext(context))).thenReturn(Some(JavaModifier.Public))
    when(javaRequiresFinalModifierPredicate.apply(eqJavaModifiersContext(context))).thenReturn(true)

    javaSupplementalModifiersResolver.resolve(context) shouldBe Set(JavaModifier.Public, JavaModifier.Final)
  }
}
