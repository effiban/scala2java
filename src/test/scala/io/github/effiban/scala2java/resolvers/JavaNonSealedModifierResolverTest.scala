package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.classifiers.{ModsClassifier, ObjectClassifier}
import io.github.effiban.scala2java.contexts.ModifiersContext
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.Unknown
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope}
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.{PrimaryCtors, Templates}
import org.mockito.ArgumentMatchers.any

import scala.meta.Mod.{Final, Sealed}
import scala.meta.Stat.WithMods
import scala.meta.{Defn, Lit, Mod, Pat, Term, Tree, Type}

class JavaNonSealedModifierResolverTest extends UnitTestSuite {

  private val DefnClassDesc = "Defn.Class"
  private val DefnTraitDesc = "Defn.Trait"
  private val DefnObjectDesc = "Defn.Object"
  private val DefnValDesc = "Defn.Val"

  private val ModsWithSealedAndFinal = List(Mod.Sealed(), Mod.Final())
  private val ModsWithSealedOnly = List(Mod.Sealed())
  private val ModsWithFinalOnly = List(Mod.Final())

  private val ClassWithoutMods = classWithMods(Nil)
  private val TraitWithoutMods = traitWithMods(Nil)
  private val ObjectWithoutMods = objectWithMods(Nil)
  private val DefnValWithoutMods= defnValWithMods(Nil)

  private val Scenarios = Table(
    ("ScalaTreeDesc", "ScalaTree", "JavaScope", "ExpectedNonSealed"),
    (DefnClassDesc, ClassWithoutMods, JavaScope.Sealed, true),
    (DefnClassDesc, classWithMods(ModsWithSealedOnly), JavaScope.Sealed, false),
    (DefnClassDesc, classWithMods(ModsWithFinalOnly), JavaScope.Sealed, false),
    (DefnClassDesc, classWithMods(ModsWithSealedAndFinal), JavaScope.Sealed, false),
    (DefnClassDesc, ClassWithoutMods, JavaScope.Package, false),
    (DefnTraitDesc, TraitWithoutMods, JavaScope.Sealed, true),
    (DefnTraitDesc, traitWithMods(ModsWithSealedOnly), JavaScope.Sealed, false),
    (DefnTraitDesc, TraitWithoutMods, JavaScope.Package, false),
    (DefnObjectDesc, ObjectWithoutMods, JavaScope.Sealed, false),
    (DefnObjectDesc, objectWithMods(ModsWithSealedOnly), JavaScope.Sealed, false),
    (DefnObjectDesc, ObjectWithoutMods, JavaScope.Package, false),
    (DefnValDesc, DefnValWithoutMods, JavaScope.Class, false)
  )

  private val modsClassifier = mock[ModsClassifier]
  private val objectClassifier = mock[ObjectClassifier]

  private val javaNonSealedModifierResolver = new JavaNonSealedModifierResolver(modsClassifier, objectClassifier)

  forAll(Scenarios) { case (
    scalaTreeDesc: String,
    scalaTree: Tree,
    javaScope: JavaScope,
    expectedNonSealed: Boolean) =>
    test(s"A '$scalaTreeDesc' that is " +
      s"${if (includeSealed(scalaModsOf(scalaTree))) "Sealed" else "not Sealed"} and " +
      s"${if (includeFinal(scalaModsOf(scalaTree))) "Final" else "not Final"} " +
      s"in scope $javaScope " +
      s"should ${if (expectedNonSealed) "require" else "NOT require"} the Java modifier 'non-sealed'") {

      val context = ModifiersContext(
        scalaTree = scalaTree,
        javaTreeType = Unknown,
        javaScope = javaScope
      )

      when(modsClassifier.includeSealed(any[List[Mod]])).thenAnswer( (mods: List[Mod]) => includeSealed(mods))
      when(modsClassifier.includeFinal(any[List[Mod]])).thenAnswer( (mods: List[Mod]) => includeFinal(mods))

      javaNonSealedModifierResolver.resolve(context) shouldBe (if (expectedNonSealed) Some(JavaModifier.NonSealed) else None)
    }
  }

  private def classWithMods(mods: List[Mod]): Defn.Class = Defn.Class(mods, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private def traitWithMods(mods: List[Mod]): Defn.Trait = Defn.Trait(mods, Type.Name("B"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private def objectWithMods(mods: List[Mod]): Defn.Object = Defn.Object(mods, Term.Name("C"), Templates.Empty)
  private def defnValWithMods(mods: List[Mod]): Defn.Val = Defn.Val(mods, List(Pat.Var(Term.Name("x"))), Some(Type.Name("Int")), Lit.Int(3))

  private def includeFinal(mods: List[Mod]) = mods.exists(_.isInstanceOf[Final])

  private def includeSealed(mods: List[Mod]) = mods.exists(_.isInstanceOf[Sealed])

  private def scalaModsOf(scalaTree: Tree): List[Mod] = scalaTree match {
    case statWithMods: WithMods => statWithMods.mods
    case _ => Nil
  }
}
