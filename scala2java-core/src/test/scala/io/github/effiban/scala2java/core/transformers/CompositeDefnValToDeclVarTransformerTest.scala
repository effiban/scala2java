package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Defn, Lit, Pat, Term}

class CompositeDefnValToDeclVarTransformerTest extends UnitTestSuite {

  private val TheJavaScope = JavaScope.Class

  private val TheDefnVal = defnValWithName("x")
  private val TheDeclVar = declVarWithName("x")

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val transformer1 = mock[DefnValToDeclVarTransformer]
  private val transformer2 = mock[DefnValToDeclVarTransformer]

  private val compositeTransformer = new CompositeDefnValToDeclVarTransformer()

  test("transform when there are no transformers - should return empty") {
    when(extensionRegistry.defnValToDeclVarTransformers).thenReturn(Nil)

    compositeTransformer.transform(TheDefnVal, TheJavaScope) shouldBe None
  }

  test("transform when there is one transformer returning non-empty should return its result") {
    when(extensionRegistry.defnValToDeclVarTransformers).thenReturn(List(transformer1))
    when(transformer1.transform(eqTree(TheDefnVal), ArgumentMatchers.eq(TheJavaScope))).thenReturn(Some(TheDeclVar))

    compositeTransformer.transform(TheDefnVal, TheJavaScope).value.structure shouldBe TheDeclVar.structure
  }

  test("transform when there are two transformers and first returns non-empty should return result of first") {
    when(extensionRegistry.defnValToDeclVarTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(TheDefnVal), ArgumentMatchers.eq(TheJavaScope))).thenReturn(Some(TheDeclVar))

    compositeTransformer.transform(TheDefnVal, TheJavaScope).value.structure shouldBe TheDeclVar.structure
  }

  test("transform when there are two transformers, first returns empty and second returns non-empty - should return result of second") {
    when(extensionRegistry.defnValToDeclVarTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(TheDefnVal), ArgumentMatchers.eq(TheJavaScope))).thenReturn(None)
    when(transformer2.transform(eqTree(TheDefnVal), ArgumentMatchers.eq(TheJavaScope))).thenReturn(Some(TheDeclVar))

    compositeTransformer.transform(TheDefnVal, TheJavaScope).value.structure shouldBe TheDeclVar.structure
  }

  test("transform when there are two transformers, both returning empty - should return empty") {
    when(extensionRegistry.defnValToDeclVarTransformers).thenReturn(List(transformer1, transformer2))
    when(transformer1.transform(eqTree(TheDefnVal), ArgumentMatchers.eq(TheJavaScope))).thenReturn(None)
    when(transformer2.transform(eqTree(TheDefnVal), ArgumentMatchers.eq(TheJavaScope))).thenReturn(None)

    compositeTransformer.transform(TheDefnVal, TheJavaScope) shouldBe None
  }

  private def defnValWithName(name: String) = Defn.Val(Nil, List(Pat.Var(Term.Name(name))), Some(TypeNames.Int), Lit.Int(3))

  private def declVarWithName(name: String) = Decl.Var(Nil, List(Pat.Var(Term.Name(name))), TypeNames.Int)

}
