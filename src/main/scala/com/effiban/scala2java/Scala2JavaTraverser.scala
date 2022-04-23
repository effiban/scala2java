package com.effiban.scala2java

import scala.meta.Defn.Trait
import scala.meta.Mod.{Abstract, Annot, Final, Private, Protected, Sealed, ValParam}
import scala.meta.Name.Indeterminate
import scala.meta.Pat.{Alternative, Bind}
import scala.meta.Term.{AnonymousFunction, Apply, ApplyType, ApplyUnary, Ascribe, Assign, Block, Do, Eta, For, ForYield, If, New, NewAnonymous, Param, Return, Select, Super, This, Throw, Try, TryWithHandler, While}
import scala.meta.{Case, Ctor, Decl, Defn, Enumerator, Import, Importee, Importer, Init, Lit, Mod, Name, Pat, Pkg, Source, Stat, Template, Term, Tree, Type}

object Scala2JavaTraverser {

  import JavaEmitter._

  private sealed trait JavaOwnerContext

  private case object Class extends JavaOwnerContext

  private case object Interface extends JavaOwnerContext

  private case object Method extends JavaOwnerContext

  private case object Lambda extends JavaOwnerContext

  private case object NoOwner extends JavaOwnerContext

  private final val ScalaModTypeToJavaModifierName: Map[Class[_ <: Mod], String] = Map(
    classOf[Private] -> "private",
    classOf[Protected] -> "protected",
    classOf[Abstract] -> "abstract",
    classOf[Final] -> "final",
    classOf[Sealed] -> "sealed"
  )

  private final val JavaModifierNamePosition = Map(
    "private" -> 0,
    "protected" -> 0,
    "public" -> 0,
    "default" -> 0,
    "static" -> 1,
    "sealed" -> 2,
    "abstract" -> 3,
    "final" -> 4
  )

  private final val ScalaTypeNameToJavaTypeName = Map(
    "Any" -> "Object",
    "AnyRef" -> "Object",
    "Boolean" -> "boolean",
    "Byte" -> "byte",
    "Short" -> "short",
    "Int" -> "int",
    "Long" -> "long",
    "Float" -> "float",
    "Double" -> "double",
    "Unit" -> "void",
    "Seq" -> "List",
    "Vector" -> "List",
    "Option" -> "Optional",
    "Nothing" -> "Void"
  )

  private val JavaPlaceholder = "__"

  private var javaOwnerContext: JavaOwnerContext = NoOwner

  def traverse(tree: Tree): Unit = tree match {
    case source: Source => traverse(source)

    case `package`: Pkg => traverse(`package`)

    case valDecl: Decl.Val => traverse(valDecl)
    case varDecl: Decl.Var => traverse(varDecl)
    case defDecl: Decl.Def => traverse(defDecl)
    case typeDecl: Decl.Type => traverse(typeDecl)

    case valDef: Defn.Val => traverse(valDef)
    case varDef: Defn.Var => traverse(varDef)
    case defDef: Defn.Def => traverse(defDef)
    case typeDef: Defn.Type => traverse(typeDef)
    case classDef: Defn.Class => traverse(classDef)
    case traitDef: Trait => traverse(traitDef)
    case objectDef: Defn.Object => traverse(objectDef)

    case `this`: This => traverse(`this`)
    case `super`: Super => traverse(`super`)
    case termName: Term.Name => traverse(termName)
    case termSelect: Term.Select => traverse(termSelect)
    case applyUnary: ApplyUnary => traverse(applyUnary)

    case apply: Term.Apply => traverse(apply)
    case applyType: ApplyType => traverse(applyType)
    case applyInfix: Term.ApplyInfix => traverse(applyInfix)
    case assign: Assign => traverse(assign)
    case `return`: Return => traverse(`return`)
    case `throw`: Throw => traverse(`throw`)
    case ascribe: Ascribe => traverse(ascribe)
    case annotate: Term.Annotate => traverse(annotate)
    case tuple: Term.Tuple => traverse(tuple)
    case block: Block => traverse(block)
    case `if`: If => traverse(`if`)
    case `match`: Term.Match => traverse(`match`)
    case `try`: Try => traverse(`try`)
    case tryWithHandler: TryWithHandler => traverse(tryWithHandler)
    case `function`: Term.Function => traverse(`function`)
    case partialFunction: Term.PartialFunction => traverse(partialFunction)
    case anonFunction: Term.AnonymousFunction => traverse(anonFunction)
    case `while`: While => traverse(`while`)
    case `do`: Do => traverse(`do`)
    case `for`: For => traverse(`for`)
    case forYield: ForYield => traverse(forYield)
    case `new`: New => traverse(`new`)
    case newAnonymous: NewAnonymous => traverse(newAnonymous)
    case placeholder: Term.Placeholder => traverse(placeholder)
    case eta: Eta => traverse(eta)
    case repeated: Term.Repeated => traverse(repeated)
    case param: Term.Param => traverse(param)
    case interpolate: Term.Interpolate => traverse(interpolate)
    case xml: Term.Xml => traverse(xml)

    case typeName: Type.Name => traverse(typeName)
    case typeSelect: Type.Select => traverse(typeSelect)
    case typeProject: Type.Project => traverse(typeProject)
    case typeSingleton: Type.Singleton => traverse(typeSingleton)

    case typeApply: Type.Apply => traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix => traverse(typeApplyInfix)
    case functionType: Type.Function => traverse(functionType)
    case tupleType: Type.Tuple => traverse(tupleType)
    case withType: Type.With => traverse(withType)
    case typeRefine: Type.Refine => traverse(typeRefine)
    case existentialType: Type.Existential => traverse(existentialType)
    case typeAnnotation: Type.Annotate => traverse(typeAnnotation)
    case lambdaType: Type.Lambda => traverse(lambdaType)
    case placeholderType: Type.Placeholder => traverse(placeholderType)
    case typeBounds: Type.Bounds => traverse(typeBounds)
    case byNameType: Type.ByName => traverse(byNameType)
    case repeatedType: Type.Repeated => traverse(repeatedType)
    case typeVar: Type.Var => traverse(typeVar)
    case typeParam: Type.Param => traverse(typeParam)

    case literal: Lit => traverse(literal)
    case patternWildcard: Pat.Wildcard => traverse(patternWildcard)
    case patternSeqWildcard: Pat.SeqWildcard => traverse(patternSeqWildcard)
    case patternVar: Pat.Var => traverse(patternVar)
    case patternBind: Bind => traverse(patternBind)
    case patternAlternative: Alternative => traverse(patternAlternative)
    case patternTuple: Pat.Tuple => traverse(patternTuple)
    case patternExtract: Pat.Extract => traverse(patternExtract)
    case patternExtractInfix: Pat.ExtractInfix => traverse(patternExtractInfix)
    case patternInterpolate: Pat.Interpolate => traverse(patternInterpolate)
    case patternXml: Pat.Xml => traverse(patternXml)
    case patternTyped: Pat.Typed => traverse(patternTyped)

    case `case`: Case => traverse(`case`)

    case anonymousName: Name.Anonymous => traverse(anonymousName)
    case indeterminateName: Indeterminate => traverse(indeterminateName)

    case template: Template => traverse(template)

    case `import`: Import => traverse(`import`)
    case importer: Importer => traverse(importer)

    case annotation: Mod.Annot => traverse(annotation)

    case init: Init => traverse(init)

    case _ => emitComment(s"UNPARSEABLE: $tree")
  }

  ////////////// TREE TRAVERSERS /////////////////

  private def traverse(source: Source): Unit = {
    source.stats.foreach(traverse)
  }

  private def traverse(pkg: Pkg): Unit = {
    emit("package ")
    traverse(pkg.ref)
    emitStatementEnd()
    emitLine()
    pkg.stats.foreach(traverse)
  }


  private def traverse(valDecl: Decl.Val): Unit = {
    val annotationsOnSameLine = valDecl.mods.exists(_.isInstanceOf[ValParam])
    traverseAnnotations(valDecl.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaOwnerContext match {
      case Class => resolveJavaClassDataMemberExplicitModifiers(mods)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a local var is 'final'
      case Method => resolveJavaExplicitModifiers(mods, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    traverse(valDecl.decltpe)
    emit(" ")
    valDecl.pats.foreach(traverse)
  }

  private def traverse(varDecl: Decl.Var): Unit = {
    val annotationsOnSameLine = varDecl.mods.exists(_.isInstanceOf[ValParam])
    traverseAnnotations(varDecl.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val modifierNames = javaOwnerContext match {
      case Class => resolveJavaClassDataMemberExplicitModifiers(varDecl.mods)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    traverse(varDecl.decltpe)
    emit(" ")
    varDecl.pats.foreach(traverse)
  }

  private def traverse(defDecl: Decl.Def): Unit = {
    emitLine()
    traverseAnnotations(defDecl.mods.collect { case ann: Annot => ann })
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => resolveJavaInterfaceMethodExplicitModifiers(defDecl.mods, hasBody = false)
      case Class => resolveJavaClassMethodExplicitModifiers(defDecl.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    traverse(defDecl.decltpe)
    emit(s" ${toJavaName(defDecl.name)}")
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    traverseMethodParams(defDecl)
    javaOwnerContext = outerJavaOwnerContext
  }

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  private def traverse(typeDecl: Decl.Type): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaInterfaceExplicitModifiers(typeDecl.mods),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    traverseGenericTypeList(typeDecl.tparams)
    // TODO handle bounds properly
  }


  private def traverse(valDef: Defn.Val): Unit = {
    val annotationsOnSameLine = valDef.mods.exists(_.isInstanceOf[ValParam])
    traverseAnnotations(valDef.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val mods = valDef.mods :+ Final()
    val modifierNames = mods match {
      case modifiers if javaOwnerContext == Class => resolveJavaClassDataMemberExplicitModifiers(modifiers)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a method param or local var is 'final' (if it's immutable as determined above)
      case modifiers if javaOwnerContext == Method => resolveJavaExplicitModifiers(modifiers, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    valDef.decltpe match {
      case Some(declType) =>
        traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
      case _ =>
    }
    valDef.pats.foreach(traverse)
    emit(" = ")
    traverse(valDef.rhs)
  }

  private def traverse(varDef: Defn.Var): Unit = {
    val annotationsOnSameLine = varDef.mods.exists(_.isInstanceOf[ValParam])
    traverseAnnotations(varDef.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val modifierNames = varDef.mods match {
      case modifiers if javaOwnerContext == Class => resolveJavaClassDataMemberExplicitModifiers(modifiers)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    varDef.decltpe match {
      case Some(declType) =>
        traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
      case _ =>
    }
    varDef.pats.foreach(traverse)
    varDef.rhs.foreach { rhs =>
      emit(" = ")
      traverse(rhs)
    }
  }

  private def traverse(defDef: Defn.Def): Unit = {
    emitLine()
    traverseAnnotations(defDef.mods.collect { case ann: Annot => ann })
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => resolveJavaInterfaceMethodExplicitModifiers(defDef.mods, hasBody = true)
      case Class => resolveJavaClassMethodExplicitModifiers(defDef.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    defDef.decltpe.foreach(traverse)
    emit(s" ${toJavaName(defDef.name)}")
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    traverseMethodParamsAndBody(defDef)
    javaOwnerContext = outerJavaOwnerContext
  }

  // Scala type definition : Closest thing in Java is an empty interface extending the same RHS
  private def traverse(typeDef: Defn.Type): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaInterfaceExplicitModifiers(typeDef.mods),
      typeKeyword = "interface",
      name = typeDef.name.toString)
    traverseGenericTypeList(typeDef.tparams)
    emit(" extends ") // TODO handle bounds properly
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    traverse(typeDef.body)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    val annotations = classDef.mods
      .filter(_.isInstanceOf[Annot])
      .map(_.asInstanceOf[Annot])
    traverseAnnotations(annotations)

    if (classDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      traverseCaseClassDef(classDef)
    } else {
      traverseRegularClassDef(classDef)
    }
  }

  private def traverseCaseClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaClassExplicitModifiers(classDef.mods),
      typeKeyword = "record",
      name = classDef.name.toString)
    // TODO - traverse type params
    emitParametersStart()
    traverse(classDef.ctor.paramss.flatten)
    emitParametersEnd()
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    traverse(classDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseRegularClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaClassExplicitModifiers(classDef.mods),
      typeKeyword = "class",
      name = classDef.name.toString)
    // TODO - traverse type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    traverseTemplate(template = classDef.templ,
      maybeExplicitPrimaryCtor = Some(classDef.ctor),
      maybeClassName = Some(classDef.name))
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverse(traitDef: Trait): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaInterfaceExplicitModifiers(traitDef.mods),
      typeKeyword = "interface",
      name = traitDef.name.toString)
    // TODO - traverse type params
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    traverse(traitDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverse(objectDef: Defn.Object): Unit = {
    emitLine()
    emitComment("originally a Scala object")
    emitLine()
    emitTypeDeclaration(modifiers = resolveJavaClassExplicitModifiers(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    traverse(objectDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }


  private def traverse(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() => emit("this")
      case name => traverse(name)
    }
  }

  private def traverse(`super`: Super): Unit = {
    `super`.thisp match {
      case Name.Anonymous() =>
      case name =>
        traverse(name)
        emit(".")
    }
    `super`.superp match {
      case Name.Anonymous() => emit("super")
      case name => traverse(name)
    }
  }

  private def traverse(name: Term.Name): Unit = {
    emit(toJavaName(name))
  }

  // qualified name
  private def traverse(termSelect: Term.Select): Unit = {
    val adjustedTermRef = termSelect match {
      case Select(Select(Term.Name("scala"), Term.Name("util")), name) => name
      case Select(Select(Term.Name("scala"), Term.Name("package")), name) => name
      case Select(Select(Term.Name("scala"), Term.Name("Predef")), name) => name
      case Select(Select(Term.Name("_root_"), Term.Name("scala")), name) => name
      case Select(Term.Name("scala"), name) => name
      case _ => termSelect
    }

    adjustedTermRef match {
      case select: Select =>
        traverse(select.qual)
        emit(".")
        traverse(select.name)
      case name: Term.Name => traverse(name)
    }
  }

  private def traverse(applyUnary: ApplyUnary): Unit = {
    traverse(applyUnary.op)
    traverse(applyUnary.arg)
  }


  // method invocation
  private def traverse(termApply: Term.Apply): Unit = {
    traverse(termApply.fun)
    emitParametersStart()
    traverse(termApply.args)
    emitParametersEnd()
  }

  // parametrized type application, e.g.: classOf[X], identity[X], List[X]
  private def traverse(termApplyType: ApplyType): Unit = {
    termApplyType.fun match {
      case Term.Name("classOf") =>
        termApplyType.targs match {
          case arg :: _ =>
            traverse(arg)
            emit(".class")
          case _ => emit(s"UNPARSEABLE class type: $termApplyType")
        }
      case fun =>
        traverse(fun)
        if (termApplyType.targs.nonEmpty) {
          emit(".")
        }
        traverseGenericTypeList(termApplyType.targs)
    }
  }

  // Infix method invocation, e.g.: a + b
  private def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    traverse(termApplyInfix.lhs)
    emit(" ")
    traverse(termApplyInfix.op)
    emit(" ")
    traverse(termApplyInfix.args)
  }

  // Variable assignment
  private def traverse(assign: Assign): Unit = {
    traverse(assign.lhs)
    emit(" = ")
    traverse(assign.rhs)
  }

  private def traverse(`return`: Return): Unit = {
    emit("return ")
    traverse(`return`.expr)
  }

  private def traverse(`throw`: Throw): Unit = {
    emit("throw ")
    traverse(`throw`.expr)
    emitStatementEnd()
  }

  // Explicitly specified type, e.g.: x = 2:Short
  // Java equivalent is casting
  private def traverse(ascribe: Ascribe): Unit = {
    emit("(")
    traverse(ascribe.tpe)
    emit(")")
    traverse(ascribe.expr)
  }

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered if it is a Java annotation
  private def traverse(termAnnotation: Term.Annotate): Unit = {
    emit("(")
    traverseAnnotations(termAnnotation.annots, onSameLine = true)
    traverse(termAnnotation.expr)
    emit(")")
  }

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it anyway
  private def traverse(termTuple: Term.Tuple): Unit = {
    emit("(")
    traverse(termTuple.args)
    emit(")")
  }

  // block of code
  private def traverse(block: Block): Unit = {
    emitBlockStart()
    traverseBlockContents(block)
    emitBlockEnd()
  }

  private def traverse(`if`: If): Unit = {
    // TODO handle mods (what is this in an 'if'?...)
    emit("if (")
    traverse(`if`.cond)
    emit(")")
    `if`.thenp match {
      case block: Block => traverse(block)
      case stmt =>
        emitBlockStart()
        traverseLastStatement(stmt)
        emitBlockEnd()
    }
    `if`.elsep match {
      case block: Block =>
        emit("else")
        traverse(block)
      case stmt =>
        emit("else")
        emitBlockStart()
        traverseLastStatement(stmt)
        emitBlockEnd()
    }
  }

  private def traverse(termMatch: Term.Match): Unit = {
    // TODO handle mods (what is this in a 'match'?...)
    emit("switch ")
    emit("(")
    traverse(termMatch.expr)
    emit(")")
    emitBlockStart()
    termMatch.cases.foreach(traverse)
    emitBlockEnd()
  }

  private def traverse(`try`: Try): Unit = {
    emit("try ")
    traverse(`try`.expr)
    `try`.catchp.foreach(traverseCatchClauseWithCase)
    `try`.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverse(tryWithHandler: TryWithHandler): Unit = {
    emit("try ")
    traverse(tryWithHandler.expr)
    traverseCatchClauseWithHandler(tryWithHandler.catchp)
    tryWithHandler.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      traverse(finallyp)
      emitBlockEnd()
    })
  }

  // lambda definition
  private def traverse(function: Term.Function): Unit = {
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Lambda
    function.params match {
      case Nil =>
      case param :: Nil => traverse(param)
      case _ =>
        emitParametersStart()
        traverse(function.params)
        emitParametersEnd()
    }
    emitArrow()
    traverse(function.body)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverse(partialFunction: Term.PartialFunction): Unit = {
    val dummyArgName = "arg"
    emit(dummyArgName)
    emitArrow()
    traverse(Term.Match(expr = Term.Name(dummyArgName), cases = partialFunction.cases))
  }

  private def traverse(anonymousFunction: AnonymousFunction): Unit = {
    traverse(Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = anonymousFunction.body))
  }

  private def traverse(`while`: While): Unit = {
    emit("while (")
    traverse(`while`.expr)
    emit(") ")
    traverse(`while`.body)
  }

  private def traverse(`do`: Do): Unit = {
    emit("do ")
    traverse(`do`.body)
    emit("while (")
    traverse(`do`.expr)
    emit(")")
  }

  private def traverse(`for`: For): Unit = {
    traverseFor(`for`.enums, `for`.body)
  }

  private def traverse(forYield: ForYield): Unit = {
    traverseFor(forYield.enums, forYield.body)
  }

  private def traverse(`new`: New): Unit = {
    emit("new ")
    traverse(`new`.init)
  }

  private def traverse(newAnonymous: NewAnonymous): Unit = {
    emit("new ")
    traverse(newAnonymous.templ)
  }

  // Underscore as expression - won't compile in java directly unless it is an anonymous function
  private def traverse(ignored: Term.Placeholder): Unit = {
    emit(JavaPlaceholder)
  }

  // Function expression with underscore, example:  func _
  private def traverse(eta: Term.Eta): Unit = {
    traverse(eta.expr)
    // TODO - see if can be improved
    emit(s" $JavaPlaceholder")
  }

  // Passing vararg expression, in Java probably nothing to change (?)
  private def traverse(termRepeated: Term.Repeated): Unit = {
    traverse(termRepeated.expr)
  }

  // method parameter declaration
  private def traverse(termParam: Term.Param): Unit = {
    traverseAnnotations(termParam.mods.collect { case ann: Annot => ann }, onSameLine = true)
    val mods = javaOwnerContext match {
      case Lambda => termParam.mods
      case _ => termParam.mods :+ Final()
    }
    val modifierNames = resolveJavaExplicitModifiers(mods, List(classOf[Final]))
    emitModifiers(modifierNames)
    termParam.decltpe.foreach(declType => {
      traverse(declType)
      emit(" ")
    })
    traverse(termParam.name)
  }

  private def traverse(termInterpolate: Term.Interpolate): Unit = {
    // Transform Scala string interpolation to Java String.format()
    termInterpolate.prefix match {
      case Term.Name("s") => traverse(toJavaStringFormatInvocation(termInterpolate.parts, termInterpolate.args))
      case _ => emitComment(s"UNPARSEABLE interpolation: $termInterpolate")
    }
  }

  private def traverse(termXml: Term.Xml): Unit = {
    // TODO
    emitComment(termXml.toString())
  }


  private def traverse(name: Type.Name): Unit = {
    emit(toJavaName(name))
  }

  // A scala type selecting expression like: a.B
  private def traverse(typeSelect: Type.Select): Unit = {
    traverse(typeSelect.qual)
    emit(".")
    traverse(typeSelect.name)
  }

  // A scala type projecting expression like: a#B
  private def traverse(typeProject: Type.Project): Unit = {
    traverse(typeProject.qual)
    emit(".")
    traverse(typeProject.name)
  }

  // A scala expression representing the type of a singleton like: A.type
  private def traverse(singletonType: Type.Singleton): Unit = {
    traverse(singletonType.ref)
    emit(".class")
  }


  // type with generic args, e.g. F[T]
  private def traverse(typeApply: Type.Apply): Unit = {
    traverse(typeApply.tpe)
    traverseGenericTypeList(typeApply.args)
  }

  // type with generic args in infix notation, e.g. K Map V
  private def traverse(ignored: Type.ApplyInfix): Unit = {
    // TODO
  }

  // lambda type
  private def traverse(functionType: Type.Function): Unit = {
    traverse(Type.Apply(Type.Name("Function"), functionType.params :+ functionType.res))
  }

  //tuple as type, cannot be translated directly into Java
  private def traverse(tupleType: Type.Tuple): Unit = {
    emitComment(tupleType.toString())
  }

  // type with parent, e.g.  A with B
  // approximated by Java "extends" but might not compile
  private def traverse(typeWith: Type.With): Unit = {
    traverse(typeWith.lhs)
    emit(" extends ")
    traverse(typeWith.rhs)
  }

  // A {def f: Int }
  private def traverse(refinedType: Type.Refine): Unit = {
    refinedType.tpe.foreach(traverse)
    // TODO try to convert to Java type with inheritance
    emitComment(s" ${refinedType.stats.toString()}")
  }

  // type with existential constraint e.g.:  A[B] forSome {B <: Number with Serializable}
  private def traverse(existentialType: Type.Existential): Unit = {
    traverse(existentialType.tpe)
    // TODO - convert to Java if there is one simple where clause
    emitComment(existentialType.stats.toString())
  }

  // type with annotation, e.g.: T @annot
  private def traverse(annotatedType: Type.Annotate): Unit = {
    traverseAnnotations(annotatedType.annots)
    emit(" ")
    traverse(annotatedType.tpe)
  }

  // generic lambda type [T] => (T, T)
  // supported only in some dialects (?)
  private def traverse(ignored: Type.Lambda): Unit = {
    // TODO
  }

  // _ in T[_]
  private def traverse(placeholderType: Type.Placeholder): Unit = {
    emit("?")
    traverse(placeholderType.bounds)
  }

  // Scala type bounds e.g. T[X <: Y]
  private def traverse(typeBounds: Type.Bounds): Unit = {
    // Only upper or lower bounds allowed in Java, not both - but if a Scala lower bound is `Null` it can be skipped
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        emit(" super ")
        traverse(lo)
      case (None, Some(hi)) =>
        emit(" extends ")
        traverse(hi)
      // TODO handle lower bound Null
      case _ => emitComment(typeBounds.toString)
    }
  }

  // Type by name, e.g.: =>T in f(x: => T)
  private def traverse(typeByName: Type.ByName): Unit = {
    // Java Consumer is the closest I can find
    traverse(Type.Apply(Type.Name("Consumer"), List(typeByName.tpe)))
  }

  // Vararg type,e.g.: T*
  private def traverse(repeatedType: Type.Repeated): Unit = {
    traverse(repeatedType.tpe)
    emitEllipsis()
  }

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement
  private def traverse(typeVar: Type.Var): Unit = {
    emitComment(typeVar.toString())
  }

  // Type param, e.g.: `T` in trait MyTrait[T]
  private def traverse(typeParam: Type.Param): Unit = {
    // TODO handle mods
    traverse(typeParam.name)
    traverseGenericTypeList(typeParam.tparams)
    traverse(typeParam.tbounds)
    // TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }


  private def traverse(lit: Lit): Unit = {
    val strValue = lit.value match {
      case str: Lit.String => s"\"$str\""
      case Lit.Unit => ""
      case other => other.toString
    }
    emit(strValue)
  }

  // Wildcard in pattern match expression - translates to Java "default" ?
  private def traverse(ignored: Pat.Wildcard): Unit = {
    emitComment("default")
  }

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*). Not translatable (?)
  private def traverse(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    emitComment("_*")
  }

  // Pattern match variable, e.g. `a` in case a =>
  private def traverse(patternVar: Pat.Var): Unit = {
    traverse(patternVar.name)
  }

  // Pattern match bind variable, e.g.: a @ A()
  private def traverse(patternBind: Bind): Unit = {
    // In Java (when supported) the order is reversed
    traverse(patternBind.rhs)
    emit(" ")
    traverse(patternBind.lhs)
  }

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  private def traverse(patternAlternative: Alternative): Unit = {
    traverse(patternAlternative.lhs)
    emit(", ")
    traverse(patternAlternative.rhs)
  }

  // Pattern match tuple expression, no Java equivalent
  private def traverse(patternTuple: Pat.Tuple): Unit = {
    emitComment(s"(${patternTuple.args.toString()})")
  }

  // Pattern match extractor e.g. A(a, b).
  // No Java equivalent (but consider rewriting as a guard ?)
  private def traverse(patternExtractor: Pat.Extract): Unit = {
    emitComment(s"${patternExtractor.fun}(${patternExtractor.args})")
  }

  // Pattern match extractor infix e.g. a E b.
  // No Java equivalent (but consider rewriting as a guard ?)
  private def traverse(patternExtractorInfix: Pat.ExtractInfix): Unit = {
    emitComment(s"${patternExtractorInfix.lhs} ${patternExtractorInfix.op} ${patternExtractorInfix.rhs}")
  }

  // Pattern interpolation e.g. r"Hello (.+)$name" , no Java equivalent
  private def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    emitComment(patternInterpolation.toString())
  }

  // Pattern match xml
  private def traverse(patternXml: Pat.Xml): Unit = {
    // TODO
    emitComment(patternXml.toString())
  }

  // Typed pattern expression. e.g. a: Int
  private def traverse(typedPattern: Pat.Typed): Unit = {
    traverse(typedPattern.rhs)
    emit(" ")
    traverse(typedPattern.lhs)
  }


  private def traverse(`case`: Case): Unit = {
    emit("case ")
    traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      traverse(cond)
      emit(")")
    })
    emitArrow()
    traverse(`case`.body)
    emitStatementEnd()
  }


  // Type with no explicit name, by default should be left empty in Java (except special handling for `this` and `super`, see above)
  private def traverse(anonymousName: Name.Anonymous): Unit = {
  }

  // Name that cannot be distinguished between a term and a type, so in Java we will just emit it unchanged (for example, in "import" statement)
  private def traverse(indeterminateName: Name.Indeterminate): Unit = {
    emit(indeterminateName.value)
  }

  private def traverse(`import`: Import): Unit = {
    `import`.importers match {
      case List() => emitComment("Invalid import with no inner importers")
      case importers => importers.foreach(traverse)
    }
  }

  private def traverse(importer: Importer): Unit = {
    importer.ref match {
      case Select(Term.Name("scala"), _) =>
      case ref =>
        importer.importees.foreach(importee => {
          emit("import ")
          traverse(ref)
          emit(".")
          traverse(importee)
          emitStatementEnd()
        })
    }
  }

  private def traverse(importee: Importee): Unit = {
    importee match {
      case Importee.Name(name) => traverse(name)
      case Importee.Wildcard() => emit("*")
      case Importee.Rename(name, rename) =>
        traverse(name)
        emitComment(s" Renamed in Scala to ${rename.toString}")
      case Importee.Unimport(name) =>
        traverse(name)
        emitComment(s" Hidden (unimported) in Scala")
    }
  }

  private def traverse(template: Template): Unit = {
    traverseTemplate(template, None, None)
  }

  private def traverseTemplate(template: Template,
                               maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None,
                               maybeClassName: Option[Type.Name] = None): Unit = {
    traverseTemplateInits(template.inits)
    template.self.decltpe.foreach(declType => {
      // TODO - consider translating the 'self' type into a Java parent
      emitComment(template.self.toString)
    })
    traverseTemplateBody(template.stats, maybeExplicitPrimaryCtor, maybeClassName)
  }

  private def traverse(annotation: Annot): Unit = {
    emit("@")
    traverse(annotation.init)
  }

  private def traverse(init: Init): Unit = {
    traverse(init.tpe)
    init.name match {
      case Name.Anonymous() =>
      case name =>
        emit(" ")
        traverse(name)
    }
    val args = init.argss.flatten
    if (args.nonEmpty) {
      emitParametersStart()
      traverse(init.argss.flatten)
      emitParametersEnd()
    }
  }

  ////////////// INTERNAL METHODS /////////////////

  private def traverseCatchClauseWithCase(`case`: Case): Unit = {
    emit("catch (")
    traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      traverse(cond)
      emit(")")
    })
    emit(")")
    emitBlockStart()
    traverse(`case`.body)
    emitBlockEnd()
  }

  private def traverseCatchClauseWithHandler(handler: Term): Unit = {
    emit("catch (")
    traverse(handler)
    emit(")")
  }

  private def traverseBlockContents(block: Block): Unit = {
    block.stats.slice(0, block.stats.length - 1)
      .foreach(stat => {
        traverse(stat)
        stat match {
          case _: Block =>
          case _: If =>
          case _: While =>
          case _ => emitStatementEnd()
        }
      })
    traverseLastStatement(block.stats.last)
  }

  private def traverseGenericTypeList(types: List[Tree]): Unit = {
    if (types.nonEmpty) {
      emitTypeArgsStart()
      traverse(list = types, onSameLine = true)
      emitTypeArgsEnd()
    }
  }

  private def traverseTemplateInits(inits: List[Init]): Unit = {
    val relevantInits = inits.filterNot(init => shouldSkipParent(init.name))
    if (relevantInits.nonEmpty) {
      emitParentNamesPrefix()
      traverse(relevantInits)
    }
  }

  private def shouldSkipParent(parent: Name): Boolean = {
    parent match {
      case Term.Name("AnyRef") => true
      case Term.Name("Product") => true
      case Term.Name("Serializable") => true
      case _ => false
    }
  }

  private def traverseTemplateBody(statements: List[Stat],
                                   maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None,
                                   maybeClassName: Option[Type.Name] = None): Unit = {
    emitBlockStart()
    traverseTypeMembers(statements)
    traverseDataMembers(statements)
    (maybeExplicitPrimaryCtor, maybeClassName) match {
      case (Some(primaryCtor), Some(className)) => traverseExplicitPrimaryCtor(primaryCtor, className)
      case _ =>
    }
    traverseMethods(statements)
    traverseOtherMembers(statements)
    emitBlockEnd()
  }

  private def traverseTypeMembers(statements: List[Stat]): Unit = {
    statements.collect {
      case typeDecl: Decl.Type => typeDecl
      case typeDefn: Defn.Type => typeDefn
    }.foreach(typeStat => {
      traverse(typeStat)
      emitStatementEnd()
    })
  }

  private def traverseDataMembers(statements: List[Stat]): Unit = {
    statements.collect {
      case valDecl: Decl.Val => valDecl
      case varDecl: Decl.Var => varDecl
      case valDefn: Defn.Val => valDefn
      case varDefn: Defn.Var => varDefn
    }.foreach(dataMember => {
      traverse(dataMember)
      emitStatementEnd()
    })
  }

  private def traverseMethods(statements: List[Stat]): Unit = {
    statements.collect {
      case defDecl: Decl.Def => defDecl
      case defDefn: Defn.Def => defDefn
    }.foreach(elem => traverse(elem))
  }

  private def traverseOtherMembers(statements: List[Stat]): Unit = {
    statements.foreach {
      case _: Decl.Type =>
      case _: Decl.Val =>
      case _: Decl.Var =>
      case _: Defn.Type =>
      case _: Defn.Val =>
      case _: Defn.Var =>
      case _: Decl.Def =>
      case _: Defn.Def =>
      case other => traverse(other)
    }
  }

  // Render a Java explicit primary class constructor
  private def traverseExplicitPrimaryCtor(primaryCtor: Ctor.Primary, className: Type.Name): Unit = {
    traverseAnnotations(primaryCtor.mods.collect { case annot: Annot => annot })
    emitModifiers(resolveJavaClassMethodExplicitModifiers(primaryCtor.mods))
    traverse(className)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    emitParametersStart()
    traverse(primaryCtor.paramss.flatten)
    emitParametersEnd()
    emitBlockStart()
    // Initialize members explicitly (what is done implicitly for Java records and Scala classes)
    primaryCtor.paramss.flatten.foreach(param => {
      val paramName = Term.Name(param.name.toString())
      traverse(Assign(Select(This(Name.Anonymous()), paramName), paramName))
      emitStatementEnd()
    })
    emitBlockEnd()
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def): Unit = {
    traverseMethodParams(defDef)
    // method body
    defDef.body match {
      case block: Block => traverse(block)
      case stmt: Stat =>
        emitBlockStart()
        traverseLastStatement(stmt)
        emitBlockEnd()
      case _ => emitStatementEnd()
    }
  }

  private def traverseMethodParams(defDecl: Decl.Def): Unit = {
    emitParametersStart()
    val params = defDecl.paramss.flatten
    traverse(params)
    emitParametersEnd()
  }

  private def traverseMethodParams(defDef: Defn.Def): Unit = {
    emitParametersStart()
    val params = defDef.paramss.flatten
    traverse(params)
    emitParametersEnd()
  }

  private def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.foreach(annotation => {
      traverse(annotation)
      if (onSameLine) {
        emit(" ")
      } else {
        emitLine()
      }
    })
  }

  private def traverse(list: List[_ <: Tree], onSameLine: Boolean = false): Unit = {
    list.zipWithIndex.foreach { case (tree, idx) =>
      traverse(tree)
      if (idx < list.size - 1) {
        emitListSeparator()
        if (list.size > 2 && !onSameLine) {
          emitLine()
        }
      }
    }
  }

  private def traverseLastStatement(stmt: Stat): Unit = {
    emit("return ")
    traverse(stmt)
    emitStatementEnd()
  }

  private def toJavaName(termName: Term.Name) = {
    // TODO - translate built-in Scala method names to Java equivalents
    termName.value
  }

  private def toJavaName(typeName: Type.Name) = {
    ScalaTypeNameToJavaTypeName.getOrElse(typeName.value, typeName.value)
  }

  private def toJavaStringFormatInvocation(formatParts: List[Lit], interpolationArgs: List[Term]) = {
    Apply(Select(Term.Name("String"), Term.Name("format")), List(Lit.String(formatParts.mkString("%s"))) ++ interpolationArgs)
  }

  private def resolveJavaClassExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Sealed], classOf[Final]))
    modifierNamesBuilder.result()
  }

  private def resolveJavaInterfaceExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    modifierNamesBuilder += "public"
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods, List(classOf[Sealed]))
    modifierNamesBuilder.result()
  }

  private def resolveJavaClassMethodExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Final]))
    modifierNamesBuilder.result()
  }

  private def resolveJavaInterfaceMethodExplicitModifiers(mods: List[Mod], hasBody: Boolean): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && hasBody) {
      modifierNamesBuilder += "default"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods, List(classOf[Private]))
    modifierNamesBuilder.result()
  }

  private def resolveJavaClassDataMemberExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods, List(classOf[Private], classOf[Protected], classOf[Final]))
    modifierNamesBuilder.result()
  }

  private def resolveJavaExplicitModifiers(inputMods: List[Mod], allowedMods: List[Class[_ <: Mod]]): List[String] = {
    ScalaModTypeToJavaModifierName.filter { case (mod, _) => inputMods.exists(inputMod => mod.isAssignableFrom(inputMod.getClass)) }
      .filter { case (mod, _) => allowedMods.contains(mod) }
      .map { case (_, modifierName) => modifierName }
      .toList
      .sortBy(modifierName => JavaModifierNamePosition.getOrElse(modifierName, Int.MaxValue))
  }

  private def pat2Param(pat: Pat) = {
    // TODO - improve
    val name = pat match {
      case Pat.Wildcard() => JavaPlaceholder
      case _ => pat.toString()
    }
    Param(mods = List.empty, name = Term.Name(name), decltpe = None, default = None)
  }

  private def traverseFor(enumerators: List[Enumerator], body: Term): Unit = {
    traverse(translateFor(enumerators, body))
  }

  private def translateFor(enumerators: List[Enumerator],
                           body: Term,
                           maybeCurrentParam: Option[Param] = None): Term = {
    enumerators match {
      case Nil =>
        emitComment("ERROR - for comprehension without enumerators")
        Lit.Unit()
      case theEnumerators =>
        val currentEnumerator :: nextEnumerators = theEnumerators

        val (nextParam, currentTerm) = currentEnumerator match {
          case Enumerator.Generator(pat, term) => (pat2Param(pat), term)
          case Enumerator.CaseGenerator(pat, term) => (pat2Param(pat), term)
          case Enumerator.Val(pat, term) => (pat2Param(pat), term)
          //TODO handle guard, for now returning dummy values
          case Enumerator.Guard(cond) => (Param(Nil, Term.Name(""), None, None), Lit.Unit())
        }

        val currentTranslated = maybeCurrentParam match {
          case Some(currentParam) => Term.Function(List(currentParam), currentTerm)
          case None => currentTerm
        }

        nextEnumerators match {
          case Nil =>
            // Next statement is last (yield)
            val nextTranslated = Term.Function(List(nextParam), body)
            Apply(Select(currentTranslated, Term.Name("map")), List(nextTranslated))
          case theNextEnumerators =>
            // Next statement is not last - recursively translate the rest
            val nextTranslated = translateFor(theNextEnumerators, body, Some(nextParam))
            Apply(Select(currentTranslated, Term.Name("flatMap")), List(nextTranslated))
        }
    }
  }
}
