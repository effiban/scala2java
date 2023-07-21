package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.{DeclVarRenderer, DefnDefRenderer, DefnVarRenderer}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.{DeclVarTraversalResult, DefnDefTraversalResult, DefnVarTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Ctor.Primary
import scala.meta.Term.Apply
import scala.meta.{Decl, Defn, Lit, Name, Pat, Self, Template, Term, Type, XtensionQuasiquoteTerm}

@deprecated
class DeprecatedDefnTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)
  private val TheClassOrTraitContext = ClassOrTraitContext(JavaScope.Class)

  private val declVarRenderer = mock[DeclVarRenderer]
  private val defnVarTraverser = mock[DefnVarTraverser]
  private val defnVarRenderer = mock[DefnVarRenderer]
  private val defnDefTraverser = mock[DefnDefTraverser]
  private val defnDefRenderer = mock[DefnDefRenderer]
  private val classTraverser = mock[DeprecatedClassTraverser]
  private val traitTraverser = mock[DeprecatedTraitTraverser]
  private val objectTraverser = mock[DeprecatedObjectTraverser]

  private val defnTraverser = new DeprecatedDefnTraverserImpl(
    declVarRenderer,
    defnVarTraverser,
    defnVarRenderer,
    defnDefTraverser,
    defnDefRenderer,
    classTraverser,
    traitTraverser,
    objectTraverser)


  test("traverse() for Defn.Var when Defn.Var returned") {

    val defnVar = q"private var myVar: Int = 3"
    val traversedDefnVar = q"private var myTraversedVar: Int = 33"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DefnVarTraversalResult(traversedDefnVar, javaModifiers)
    val renderContext = VarRenderContext(javaModifiers)

    doReturn(traversalResult).when(defnVarTraverser).traverse(eqTree(defnVar), eqTo(TheStatContext))

    defnTraverser.traverse(defnVar, TheStatContext)

    verify(defnVarRenderer).render(eqTree(traversalResult.tree), eqTo(renderContext))
  }

  test("traverse() for Defn.Var when Decl.Var returned") {

    val defnVar = q"private var myVar: Int = 3"
    val declVar = q"private var myVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclVarTraversalResult(declVar, javaModifiers)
    val renderContext = VarRenderContext(javaModifiers)

    doReturn(traversalResult).when(defnVarTraverser).traverse(eqTree(defnVar), eqTo(TheStatContext))

    defnTraverser.traverse(defnVar, TheStatContext)

    verify(declVarRenderer).render(eqTree(traversalResult.tree), eqTo(renderContext))
  }

  test("traverse() for Defn.Def") {
    val defnDef = q"def myMethod(x: Int) = doSomething(x)"
    val traversedDefnDef = q"def myMethod2(xx: Int) = doSomething2(xx)"
    val traversalResult = DefnDefTraversalResult(traversedDefnDef, List(JavaModifier.Public))

    doReturn(traversalResult).when(defnDefTraverser).traverse(eqTree(defnDef), eqTo(DefnDefContext(TheStatContext.javaScope)))

    defnTraverser.traverse(defnDef, TheStatContext)

    verify(defnDefRenderer).render(eqTree(traversedDefnDef), eqTo(DefRenderContext(traversalResult.javaModifiers)))
  }

  test("traverse() for Defn.Class") {

    val defnClass = Defn.Class(
      mods = List(),
      name = Type.Name("MyClass"),
      tparams = List(),
      ctor = Primary(
        mods = List(),
        name = Name.Anonymous(),
        paramss = List(List(termParam("param1", "Int"), termParam("param2", "String")))
      ),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Apply(Term.Name("doSomething"), List(Lit.String("input")))
        )
      )
    )

    defnTraverser.traverse(defnClass, TheStatContext)

    verify(classTraverser).traverse(eqTree(defnClass), eqTo(TheClassOrTraitContext))
  }

  test("traverse() for Trait") {

    val defnTrait = Defn.Trait(
      mods = List(),
      name = Type.Name("MyTrait"),
      tparams = List(),
      ctor = Primary(
        mods = List(),
        name = Name.Anonymous(),
        paramss = List()
      ),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Decl.Val(
            mods = List(),
            pats = List(Pat.Var(Term.Name("x"))),
            decltpe = TypeNames.Int
          )
        )
      )
    )

    defnTraverser.traverse(defnTrait, TheStatContext)

    verify(traitTraverser).traverse(eqTree(defnTrait), eqTo(TheClassOrTraitContext))
  }

  test("traverse() for Object") {

    val defnObject = Defn.Object(
      mods = List(),
      name = Term.Name("MyObject"),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Defn.Val(
            mods = List(),
            pats = List(Pat.Var(Term.Name("x"))),
            decltpe = Some(TypeNames.Int),
            rhs = Lit.Int(3)
          )
        )
      )
    )

    defnTraverser.traverse(defnObject, TheStatContext)

    verify(objectTraverser).traverse(eqTree(defnObject), eqTo(TheStatContext))
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
