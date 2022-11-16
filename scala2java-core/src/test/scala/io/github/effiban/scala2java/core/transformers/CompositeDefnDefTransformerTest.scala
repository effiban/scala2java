package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer

import scala.meta.{Defn, Term}

class CompositeDefnDefTransformerTest extends UnitTestSuite {

  private val InitialDefnDef = defnDefWithName("initial")

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  test("transform when there are two transformers") {
    val transformedDefnDef1 = defnDefWithName("transformed1")
    val transformedDefnDef2 = defnDefWithName("transformed2")

    val transformer1 = mock[DefnDefTransformer]
    val transformer2 = mock[DefnDefTransformer]
    val transformers = List(transformer1, transformer2)

    when(transformer1.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef1)
    when(transformer2.transform(eqTree(transformedDefnDef1))).thenReturn(transformedDefnDef2)

    when(extensionRegistry.defnDefTransformers).thenReturn(transformers)

    new CompositeDefnDefTransformer().transform(InitialDefnDef).structure shouldBe transformedDefnDef2.structure
  }

  test("transform when there is one transformer") {
    val transformedDefnDef = defnDefWithName("transformed")

    val transformer = mock[DefnDefTransformer]
    val transformers = List(transformer)

    when(transformer.transform(eqTree(InitialDefnDef))).thenReturn(transformedDefnDef)

    when(extensionRegistry.defnDefTransformers).thenReturn(transformers)

    new CompositeDefnDefTransformer().transform(InitialDefnDef).structure shouldBe transformedDefnDef.structure
  }

  test("transform when there are no transformers") {
    when(extensionRegistry.defnDefTransformers).thenReturn(Nil)

    new CompositeDefnDefTransformer().transform(InitialDefnDef).structure shouldBe InitialDefnDef.structure
  }

  private def defnDefWithName(name: String) = {
    Defn.Def(Nil, Term.Name(name), Nil, List(Nil), Some(TypeNames.Int), Term.Apply(Term.Name("doSomething"), Nil))
  }
}
