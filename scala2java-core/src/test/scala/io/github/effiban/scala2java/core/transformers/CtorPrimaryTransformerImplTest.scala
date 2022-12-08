package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.contexts.CtorContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.ArgumentMatchers.any

import scala.meta.Term.{Assign, Block, This}
import scala.meta.{Ctor, Defn, Init, Lit, Mod, Name, Term, Type}

class CtorPrimaryTransformerImplTest extends UnitTestSuite {

  private val ClassName = "MyClass"

  private val AnnotationName = "MyAnnotation"

  private val Modifiers = List(
    Mod.Annot(
      Init(tpe = Type.Name(AnnotationName), name = Name.Anonymous(), argss = List())
    )
  )

  private val CtorParams = List(
    termParamInt("param1"),
    termParamInt("param2")
  )

  private val InitArgss = List(List(Lit.String("superArg1"), Lit.String("superArg2")))

  private val ctorInitsToSuperCallTransformer: TemplateInitsToSuperCallTransformer = mock[TemplateInitsToSuperCallTransformer]

  private val ctorPrimaryTransformer = new CtorPrimaryTransformerImpl(ctorInitsToSuperCallTransformer)

  test("traverse() when has no params, no super call, no terms") {
    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = Some(Type.AnonymousName()),
      body = Block(Nil)
    )

    val context = CtorContext(javaScope = JavaScope.Class, className = Type.Name(ClassName))

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, context)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has no params, no super call, but has terms") {
    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val terms = List(
      Term.Apply(Term.Name("foo1"), Nil),
      Term.Apply(Term.Name("foo2"), Nil)
    )

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = Some(Type.AnonymousName()),
      body = Block(terms)
    )

    val context = CtorContext(
      javaScope = JavaScope.Class,
      className = Type.Name(ClassName),
      terms = terms
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, context)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has no params and has super call") {
    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val expectedSuperCall = Term.Apply(fun = Term.Name("super"), args = InitArgss.flatten)

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = Some(Type.AnonymousName()),
      body = Block(List(expectedSuperCall))
    )

    val context = CtorContext(
      javaScope = JavaScope.Class,
      className = Type.Name(ClassName)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(Some(expectedSuperCall))

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, context)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and no super call") {
    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams)
    )

    val expectedAssignments = CtorParams.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Term.Select(This(Name.Anonymous()), paramName), paramName)
    })

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = Some(Type.AnonymousName()),
      body = Block(expectedAssignments)
    )

    val context = CtorContext(javaScope = JavaScope.Class, className = Type.Name(ClassName))

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = CtorPrimaryTransformer.transform(primaryCtor, context)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and super call") {
    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams)
    )

    val inputInit =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = InitArgss
      )

    val expectedSuperCall = Term.Apply(fun = Term.Name("super"), args = InitArgss.flatten)

    val expectedAssignments = CtorParams.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Term.Select(This(Name.Anonymous()), paramName), paramName)
    })

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = Some(Type.AnonymousName()),
      body = Block(expectedSuperCall :: expectedAssignments)
    )

    val context = CtorContext(
      javaScope = JavaScope.Class,
      className = Type.Name(ClassName),
      inits = List(inputInit)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(Some(expectedSuperCall))

    val actualDefnDef = CtorPrimaryTransformer.transform(primaryCtor, context)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }
}
