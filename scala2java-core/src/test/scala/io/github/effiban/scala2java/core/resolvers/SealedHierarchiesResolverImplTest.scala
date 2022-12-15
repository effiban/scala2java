package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.ModsClassifier
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Selfs}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList

import scala.meta.{Defn, Import, Importee, Importer, Init, Mod, Name, Template, Term, Type}

class SealedHierarchiesResolverImplTest extends UnitTestSuite {

  private val SealedMods = List(Mod.Sealed())

  private val modsClassifier = mock[ModsClassifier]

  private val sealedHierarchiesResolver = new SealedHierarchiesResolverImpl(modsClassifier)

  override def beforeEach(): Unit = {
    super.beforeEach()

    when(modsClassifier.includeSealed(Nil)).thenReturn(false)
    when(modsClassifier.includeSealed(eqTreeList(SealedMods))).thenReturn(true)
  }

  test("traverse for import and class") {
    val importDef = Import(List(Importer(Term.Name("A"), List(Importee.Name(Name.Indeterminate("a"))))))
    val classDef = defnClass(Type.Name("B"))
    val stats = List(importDef, classDef)

    when(modsClassifier.includeSealed(Nil)).thenReturn(false)

    sealedHierarchiesResolver.traverse(stats) shouldBe SealedHierarchies()
  }

  test("traverse for trait and object, no inheritance") {
    val traitDef = defnTrait(Type.Name("A"))
    val objectDef = defnObject(Term.Name("B"))
    val stats = List(traitDef, objectDef)

    when(modsClassifier.includeSealed(Nil)).thenReturn(false)

    sealedHierarchiesResolver.traverse(stats) shouldBe SealedHierarchies()
  }

  test("traverse for not-sealed class and extending object") {
    val classDef = defnClass(Type.Name("A"))
    val objectDef = defnObject(name = Term.Name("B"), parentNames = List(Type.Name("A")))
    val stats = List(classDef, objectDef)

    sealedHierarchiesResolver.traverse(stats) shouldBe SealedHierarchies()
  }

  test("traverse for not-sealed trait and 2 extending objects") {
    val traitDef = defnTrait(Type.Name("A"))
    val objectDef1 = defnObject(name = Term.Name("B"), parentNames = List(Type.Name("A")))
    val objectDef2 = defnObject(name = Term.Name("C"), parentNames = List(Type.Name("A")))
    val stats = List(traitDef, objectDef1, objectDef2)

    sealedHierarchiesResolver.traverse(stats) shouldBe SealedHierarchies()
  }

  test("traverse for sealed trait and 2 extending objects") {
    val traitDef = defnTrait(name = Type.Name("A"), mods = SealedMods)
    val objectDef1 = defnObject(name = Term.Name("B"), parentNames = List(Type.Name("A")))
    val objectDef2 = defnObject(name = Term.Name("C"), parentNames = List(Type.Name("A")))
    val stats = List(traitDef, objectDef1, objectDef2)

    val expectedSealedHierarchies = SealedHierarchies(Map(Type.Name("A") -> List(Term.Name("B"), Term.Name("C"))))

    sealedHierarchiesResolver.traverse(stats).asStringMap() shouldBe expectedSealedHierarchies.asStringMap()
  }

  test("traverse for sealed trait and 2 extending classes") {
    val traitDef = defnTrait(name = Type.Name("A"), mods = SealedMods)
    val classDef1 = defnClass(name = Type.Name("B"), parentNames = List(Type.Name("A")))
    val classDef2 = defnClass(name = Type.Name("C"), parentNames = List(Type.Name("A")))
    val stats = List(traitDef, classDef1, classDef2)

    val expectedSealedHierarchies = SealedHierarchies(Map(Type.Name("A") -> List(Term.Name("B"), Term.Name("C"))))

    sealedHierarchiesResolver.traverse(stats).asStringMap() shouldBe expectedSealedHierarchies.asStringMap()
  }

  test("traverse for sealed class and 2 extending objects") {
    val classDef = defnClass(name = Type.Name("A"), mods = SealedMods)
    val objectDef1 = defnObject(name = Term.Name("B"), parentNames = List(Type.Name("A")))
    val objectDef2 = defnObject(name = Term.Name("C"), parentNames = List(Type.Name("A")))
    val stats = List(classDef, objectDef1, objectDef2)

    val expectedSealedHierarchies = SealedHierarchies(Map(Type.Name("A") -> List(Term.Name("B"), Term.Name("C"))))

    sealedHierarchiesResolver.traverse(stats).asStringMap() shouldBe expectedSealedHierarchies.asStringMap()
  }

  test("traverse for sealed class and 2 extending objects, plus one extra object") {
    val classDef = defnClass(name = Type.Name("A"), mods = SealedMods)
    val objectDef1 = defnObject(name = Term.Name("B"), parentNames = List(Type.Name("A")))
    val objectDef2 = defnObject(name = Term.Name("C"), parentNames = List(Type.Name("A")))
    val objectDef3 = defnObject(name = Term.Name("D"))
    val stats = List(classDef, objectDef1, objectDef2, objectDef3)

    val expectedSealedHierarchies = SealedHierarchies(Map(Type.Name("A") -> List(Term.Name("B"), Term.Name("C"))))

    sealedHierarchiesResolver.traverse(stats).asStringMap() shouldBe expectedSealedHierarchies.asStringMap()
  }

  test("traverse for two separate sealed hierarchies") {
    val traitDefA = defnTrait(name = Type.Name("A"), mods = SealedMods)
    val objectDefA1 = defnObject(name = Term.Name("A1"), parentNames = List(Type.Name("A")))
    val objectDefA2 = defnObject(name = Term.Name("A2"), parentNames = List(Type.Name("A")))
    val traitDefB = defnClass(name = Type.Name("B"), mods = SealedMods)
    val objectDefB1 = defnObject(name = Term.Name("B1"), parentNames = List(Type.Name("B")))
    val objectDefB2 = defnObject(name = Term.Name("B2"), parentNames = List(Type.Name("B")))

    val stats = List(
      traitDefA,
      objectDefA1,
      objectDefA2,
      traitDefB,
      objectDefB1,
      objectDefB2
    )

    val expectedSealedHierarchies = SealedHierarchies(Map(
      Type.Name("A") -> List(Term.Name("A1"), Term.Name("A2")),
      Type.Name("B") -> List(Term.Name("B1"), Term.Name("B2")))
    )

    sealedHierarchiesResolver.traverse(stats).asStringMap() shouldBe expectedSealedHierarchies.asStringMap()
  }

  test("traverse for two nested sealed hierarchies") {
    val traitDefA = defnTrait(name = Type.Name("A"), mods = SealedMods)
    val objectDefA1 = defnObject(name = Term.Name("A1"), parentNames = List(Type.Name("A")))
    val objectDefA2 = defnObject(name = Term.Name("A2"), parentNames = List(Type.Name("A")))
    val traitDefB = defnTrait(name = Type.Name("B"), parentNames = List(Type.Name("A")), mods = SealedMods)
    val objectDefB1 = defnObject(name = Term.Name("B1"), parentNames = List(Type.Name("B")))
    val objectDefB2 = defnObject(name = Term.Name("B2"), parentNames = List(Type.Name("B")))

    val stats = List(
      traitDefA,
      objectDefA1,
      objectDefA2,
      traitDefB,
      objectDefB1,
      objectDefB2
    )

    val expectedSealedHierarchies = SealedHierarchies(Map(
      Type.Name("A") -> List(Term.Name("A1"), Term.Name("A2"), Type.Name("B")),
      Type.Name("B") -> List(Term.Name("B1"), Term.Name("B2")))
    )

    sealedHierarchiesResolver.traverse(stats).asStringMap() shouldBe expectedSealedHierarchies.asStringMap()
  }

  private def defnTrait(name: Type.Name, parentNames: List[Type.Name] = Nil, mods: List[Mod] = Nil): Defn.Trait = {
    Defn.Trait(
      mods = mods,
      name = name,
      tparams = List(),
      ctor = PrimaryCtors.Empty,
      templ = Template(
        early = List(),
        inits = parentNames.map(initOf),
        self = Selfs.Empty,
        stats = Nil
      )
    )
  }

  private def defnClass(name: Type.Name, parentNames: List[Type.Name] = Nil, mods: List[Mod] = Nil): Defn.Class = {
    Defn.Class(
      mods = mods,
      name = name,
      tparams = List(),
      ctor = PrimaryCtors.Empty,
      templ = Template(
        early = List(),
        inits = parentNames.map(initOf),
        self = Selfs.Empty,
        stats = Nil
      )
    )
  }

  private def defnObject(name: Term.Name, parentNames: List[Type.Name] = Nil, mods: List[Mod] = Nil): Defn.Object = {
    Defn.Object(
      mods = mods,
      name = name,
      templ = Template(
        early = List(),
        inits = parentNames.map(initOf),
        self = Selfs.Empty,
        stats = Nil
      )
    )
  }

  private def initOf(parentName: Type.Name) = Init(tpe = parentName, name = Name.Anonymous(), argss = List(Nil))
}
