package com.effiban.scala2java.transformers

import com.effiban.scala2java.TraversalContext.javaOwnerContext
import com.effiban.scala2java.testtrees.TypeNames
import com.effiban.scala2java.{Class, UnitTestSuite}
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

  private val ctorInitsToSuperCallTransformer: TemplateInitsToSuperCallTransformer = mock[TemplateInitsToSuperCallTransformer]

  private val ctorPrimaryTransformer = new CtorPrimaryTransformerImpl(ctorInitsToSuperCallTransformer)

  test("traverse() when has no params and no super call") {
    javaOwnerContext = Class

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
      decltpe = None,
      body = Block(Nil)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, Type.Name(ClassName), Nil)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has no params and has super call") {
    javaOwnerContext = Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = Nil
    )

    val inputInitArgss = List(List(Lit.String("superArg1"), Lit.String("superArg2")))

    val inputInit =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = inputInitArgss
      )

    val expectedSuperCall = Term.Apply(fun = Term.Name("super"), args = inputInitArgss.flatten)

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = Nil,
      decltpe = None,
      body = Block(List(expectedSuperCall))
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(Some(expectedSuperCall))

    val actualDefnDef = ctorPrimaryTransformer.transform(primaryCtor, Type.Name(ClassName), List(inputInit))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }


  test("traverse() when has params and no super call") {
    javaOwnerContext = Class

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
      decltpe = None,
      body = Block(expectedAssignments)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(None)

    val actualDefnDef = CtorPrimaryTransformer.transform(primaryCtor, Type.Name(ClassName), Nil)

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }

  test("traverse() when has params and super call") {
    javaOwnerContext = Class

    val primaryCtor = Ctor.Primary(
      mods = Modifiers,
      name = Name.Anonymous(),
      paramss = List(CtorParams)
    )

    val inputInitArgss = List(List(Lit.String("superArg1"), Lit.String("superArg2")))

    val inputInit =
      Init(
        tpe = Type.Name("MySuperClass"),
        name = Name.Anonymous(),
        argss = inputInitArgss
      )

    val expectedSuperCall = Term.Apply(fun = Term.Name("super"), args = inputInitArgss.flatten)

    val expectedAssignments = CtorParams.map(param => {
      val paramName = Term.Name(param.name.toString())
      Assign(Term.Select(This(Name.Anonymous()), paramName), paramName)
    })

    val expectedDefnDef = Defn.Def(
      mods = primaryCtor.mods,
      name = Term.Name(ClassName),
      tparams = Nil,
      paramss = List(CtorParams),
      decltpe = None,
      body = Block(expectedSuperCall :: expectedAssignments)
    )

    when(ctorInitsToSuperCallTransformer.transform(any())).thenReturn(Some(expectedSuperCall))

    val actualDefnDef = CtorPrimaryTransformer.transform(primaryCtor, Type.Name(ClassName), List(inputInit))

    actualDefnDef.structure shouldBe expectedDefnDef.structure
  }
  
  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }
}
