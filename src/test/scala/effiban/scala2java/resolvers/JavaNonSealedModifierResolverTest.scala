package effiban.scala2java.resolvers

import effiban.scala2java.classifiers.{ModsClassifier, ObjectClassifier}
import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.Unknown
import effiban.scala2java.entities.{JavaModifier, JavaScope}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, Templates}

import scala.meta.{Defn, Lit, Mod, Pat, Term, Tree, Type}

class JavaNonSealedModifierResolverTest extends UnitTestSuite {

  private final val ModsIncludeSealed = true
  private final val ModsDontIncludeSealed = false
  private final val ModsIncludeFinal = true
  private final val ModsDontIncludeFinal = false

  private val TheDefnClass = Defn.Class(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheDefnTrait = Defn.Trait(Nil, Type.Name("B"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheDefnObject = Defn.Object(Nil, Term.Name("C"), Templates.Empty)
  private val TheDefnVal = Defn.Val(Nil, List(Pat.Var(Term.Name("x"))), Some(Type.Name("Int")), Lit.Int(3))

  private val DummyMods: List[Mod] = List(Mod.Implicit())

  private val NonObjectScenarios = Table(
    ("ScalaTreeDesc", "ScalaTree", "ScalaModsIncludeSealed", "ScalaModsIncludeFinal", "JavaScope", "ExpectedNonSealed"),
    ("Defn.Class", TheDefnClass, ModsDontIncludeSealed, ModsDontIncludeFinal, JavaScope.Sealed, true),
    ("Defn.Class", TheDefnClass, ModsIncludeSealed, ModsDontIncludeFinal, JavaScope.Sealed, false),
    ("Defn.Class", TheDefnClass, ModsDontIncludeSealed, ModsIncludeFinal, JavaScope.Sealed, false),
    ("Defn.Class", TheDefnClass, ModsIncludeSealed, ModsIncludeFinal, JavaScope.Sealed, false),
    ("Defn.Class", TheDefnClass, ModsDontIncludeSealed, ModsDontIncludeFinal, JavaScope.Package, false),
    ("Defn.Trait", TheDefnTrait, ModsDontIncludeSealed, ModsDontIncludeFinal, JavaScope.Sealed, true),
    ("Defn.Trait", TheDefnTrait, ModsIncludeSealed, ModsDontIncludeFinal, JavaScope.Sealed, false),
    ("Defn.Trait", TheDefnTrait, ModsDontIncludeSealed, ModsDontIncludeFinal, JavaScope.Package, false),
    ("Defn.Val", TheDefnVal, ModsDontIncludeSealed, ModsDontIncludeFinal, JavaScope.Class, false)
  )

  private val modsClassifier = mock[ModsClassifier]
  private val objectClassifier = mock[ObjectClassifier]

  private val javaNonSealedModifierResolver = new JavaNonSealedModifierResolver(modsClassifier, objectClassifier)

  forAll(NonObjectScenarios) { case (
    scalaTreeDesc: String,
    scalaTree: Tree,
    scalaModsIncludeSealed: Boolean,
    scalaModsIncludeFinal: Boolean,
    javaScope: JavaScope,
    expectedNonSealed: Boolean) =>
    test(s"A '$scalaTreeDesc' that is " +
      s"${if (scalaModsIncludeSealed) "Sealed" else "not Sealed"} and " +
      s"${if (scalaModsIncludeFinal) "Final" else "not Final"} " +
      s"in scope $javaScope " +
      s"should ${if (expectedNonSealed) "require" else "NOT require"} the Java modifier 'non-sealed'") {

      val context = JavaModifiersContext(
        scalaTree = scalaTree,
        scalaMods = DummyMods,
        javaTreeType = Unknown,
        javaScope = javaScope
      )

      when(modsClassifier.includeSealed(DummyMods)).thenReturn(scalaModsIncludeSealed)
      when(modsClassifier.includeFinal(DummyMods)).thenReturn(scalaModsIncludeFinal)

      javaNonSealedModifierResolver.resolve(context) shouldBe (if (expectedNonSealed) Some(JavaModifier.NonSealed) else None)
    }
  }

  test("A Defn.Object that is standalone in Java scope Sealed should NOT require the Java modifier 'non-sealed'") {
    val context = JavaModifiersContext(
      scalaTree = TheDefnObject,
      scalaMods = DummyMods,
      javaTreeType = Unknown,
      javaScope = JavaScope.Sealed
    )

    when(objectClassifier.isStandalone(eqTree(TheDefnObject))).thenReturn(true)

    javaNonSealedModifierResolver.resolve(context) shouldBe None
  }

  test("A Defn.Object that is NOT standalone, in Java scope Sealed with no other mods should require the Java modifier 'non-sealed'") {
    val context = JavaModifiersContext(
      scalaTree = TheDefnObject,
      scalaMods = DummyMods,
      javaTreeType = Unknown,
      javaScope = JavaScope.Sealed
    )

    when(objectClassifier.isStandalone(eqTree(TheDefnObject))).thenReturn(false)
    when(modsClassifier.includeSealed(DummyMods)).thenReturn(false)
    when(modsClassifier.includeFinal(DummyMods)).thenReturn(false)

    javaNonSealedModifierResolver.resolve(context) shouldBe Some(JavaModifier.NonSealed)
  }

  test("A Defn.Object that is NOT standalone, is Java scope Sealed and has mod Sealed should NOT require the Java modifier 'non-sealed'") {
    val context = JavaModifiersContext(
      scalaTree = TheDefnObject,
      scalaMods = DummyMods,
      javaTreeType = Unknown,
      javaScope = JavaScope.Sealed
    )

    when(objectClassifier.isStandalone(eqTree(TheDefnObject))).thenReturn(false)
    when(modsClassifier.includeSealed(DummyMods)).thenReturn(true)
    when(modsClassifier.includeFinal(DummyMods)).thenReturn(false)

    javaNonSealedModifierResolver.resolve(context) shouldBe None
  }

  test("A Defn.Object that is NOT standalone, and not in Java scope Sealed should NOT require the Java modifier 'non-sealed'") {
    val context = JavaModifiersContext(
      scalaTree = TheDefnObject,
      scalaMods = DummyMods,
      javaTreeType = Unknown,
      javaScope = JavaScope.Package
    )

    when(objectClassifier.isStandalone(eqTree(TheDefnObject))).thenReturn(false)

    javaNonSealedModifierResolver.resolve(context) shouldBe None
  }
}
