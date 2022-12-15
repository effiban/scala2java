package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Templates}
import io.github.effiban.scala2java.spi.transformers.ClassTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Defn, Type}

class CompositeClassTransformerTest extends UnitTestSuite {

  private val InitialClass = classDefOf("Initial")

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  test("transform when there are two transformers") {
    val transformedClass1 = classDefOf("Transformed1")
    val transformedClass2 = classDefOf("Transformed2")

    val transformer1 = mock[ClassTransformer]
    val transformer2 = mock[ClassTransformer]
    val transformers = List(transformer1, transformer2)

    when(transformer1.transform(eqTree(InitialClass))).thenReturn(transformedClass1)
    when(transformer2.transform(eqTree(transformedClass1))).thenReturn(transformedClass2)

    when(extensionRegistry.classTransformers).thenReturn(transformers)

    new CompositeClassTransformer().transform(InitialClass).structure shouldBe transformedClass2.structure
  }

  test("transform when there is one transformer") {
    val transformedClass = classDefOf("Transformed")

    val transformer = mock[ClassTransformer]
    val transformers = List(transformer)

    when(transformer.transform(eqTree(InitialClass))).thenReturn(transformedClass)

    when(extensionRegistry.classTransformers).thenReturn(transformers)

    new CompositeClassTransformer().transform(InitialClass).structure shouldBe transformedClass.structure
  }

  test("transform when there are no transformers") {
    when(extensionRegistry.classTransformers).thenReturn(Nil)

    new CompositeClassTransformer().transform(InitialClass).structure shouldBe InitialClass.structure
  }

  private def classDefOf(name: String) = {
    Defn.Class(
      mods = Nil,
      name = Type.Name(name),
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )
  }
}
