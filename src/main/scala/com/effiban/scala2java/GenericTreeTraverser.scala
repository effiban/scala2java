package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn.Trait
import scala.meta.Mod.{Abstract, Annot, Final, Private, Protected, Sealed}
import scala.meta.Name.Indeterminate
import scala.meta.Pat.{Alternative, Bind}
import scala.meta.Term.{AnonymousFunction, Apply, ApplyType, ApplyUnary, Ascribe, Assign, Block, Do, Eta, For, ForYield, If, New, NewAnonymous, Param, Return, Select, Super, This, Throw, Try, TryWithHandler, While}
import scala.meta.{Case, Ctor, Decl, Defn, Enumerator, Import, Importee, Importer, Init, Lit, Mod, Name, Pat, Pkg, Source, Stat, Template, Term, Tree, Type}

object GenericTreeTraverser extends ScalaTreeTraverser[Tree] {

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

  override def traverse(tree: Tree): Unit = tree match {
    case source: Source => SourceTraverser.traverse(source)

    case pkg: Pkg => PkgTraverser.traverse(pkg)

    case valDecl: Decl.Val => DeclValTraverser.traverse(valDecl)
    case varDecl: Decl.Var => DeclVarTraverser.traverse(varDecl)
    case defDecl: Decl.Def => DeclDefTraverser.traverse(defDecl)
    case typeDecl: Decl.Type => DeclTypeTraverser.traverse(typeDecl)

    case valDef: Defn.Val => DefnValTraverser.traverse(valDef)
    case varDef: Defn.Var => DefnVarTraverser.traverse(varDef)
    case defDef: Defn.Def => DefnDefTraverser.traverse(defDef)
    case typeDef: Defn.Type => DefnTypeTraverser.traverse(typeDef)
    case classDef: Defn.Class => ClassTraverser.traverse(classDef)
    case traitDef: Trait => TraitTraverser.traverse(traitDef)
    case objectDef: Defn.Object => ObjectTraverser.traverse(objectDef)

    case `this`: This => ThisTraverser.traverse(`this`)
    case `super`: Super => SuperTraverser.traverse(`super`)
    case termName: Term.Name => TermNameTraverser.traverse(termName)
    case termSelect: Term.Select => TermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => ApplyUnaryTraverser.traverse(applyUnary)

    case apply: Term.Apply => TermApplyTraverser.traverse(apply)
    case applyType: ApplyType => ApplyTypeTraverser.traverse(applyType)
    case applyInfix: Term.ApplyInfix => TermApplyInfixTraverser.traverse(applyInfix)
    case assign: Assign => AssignTraverser.traverse(assign)
    case `return`: Return => ReturnTraverser.traverse(`return`)
    case `throw`: Throw => ThrowTraverser.traverse(`throw`)
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

    case _ => emitComment(s"UNRECOGNIZED: $tree")
  }

  ////////////// TREE TRAVERSERS /////////////////

  // Explicitly specified type, e.g.: x = 2:Short
  // Java equivalent is casting
  def traverse(ascribe: Ascribe): Unit = {
    emit("(")
    traverse(ascribe.tpe)
    emit(")")
    traverse(ascribe.expr)
  }

  // Expression annotation, e.g.:  (x: @annot) match ....
  // Partially supported in Java, so it will be rendered if it is a Java annotation
  def traverse(termAnnotation: Term.Annotate): Unit = {
    emit("(")
    traverseAnnotations(termAnnotation.annots, onSameLine = true)
    traverse(termAnnotation.expr)
    emit(")")
  }

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it anyway
  def traverse(termTuple: Term.Tuple): Unit = {
    emit("(")
    traverse(termTuple.args)
    emit(")")
  }

  // block of code
  def traverse(block: Block): Unit = {
    emitBlockStart()
    traverseBlockContents(block)
    emitBlockEnd()
  }

  def traverse(`if`: If): Unit = {
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

  def traverse(termMatch: Term.Match): Unit = {
    // TODO handle mods (what is this in a 'match'?...)
    emit("switch ")
    emit("(")
    traverse(termMatch.expr)
    emit(")")
    emitBlockStart()
    termMatch.cases.foreach(traverse)
    emitBlockEnd()
  }

  def traverse(`try`: Try): Unit = {
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

  def traverse(tryWithHandler: TryWithHandler): Unit = {
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
  def traverse(function: Term.Function): Unit = {
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

  def traverse(partialFunction: Term.PartialFunction): Unit = {
    val dummyArgName = "arg"
    emit(dummyArgName)
    emitArrow()
    traverse(Term.Match(expr = Term.Name(dummyArgName), cases = partialFunction.cases))
  }

  def traverse(anonymousFunction: AnonymousFunction): Unit = {
    traverse(Term.Function(
      params = List(Param(name = Term.Name(JavaPlaceholder), mods = Nil, decltpe = None, default = None)),
      body = anonymousFunction.body))
  }

  def traverse(`while`: While): Unit = {
    emit("while (")
    traverse(`while`.expr)
    emit(") ")
    traverse(`while`.body)
  }

  def traverse(`do`: Do): Unit = {
    emit("do ")
    traverse(`do`.body)
    emit("while (")
    traverse(`do`.expr)
    emit(")")
  }

  def traverse(`for`: For): Unit = {
    traverseFor(`for`.enums, `for`.body)
  }

  def traverse(forYield: ForYield): Unit = {
    traverseFor(forYield.enums, forYield.body)
  }

  def traverse(`new`: New): Unit = {
    emit("new ")
    traverse(`new`.init)
  }

  def traverse(newAnonymous: NewAnonymous): Unit = {
    emit("new ")
    traverse(newAnonymous.templ)
  }

  // Underscore as expression - won't compile in java directly unless it is an anonymous function
  def traverse(ignored: Term.Placeholder): Unit = {
    emit(JavaPlaceholder)
  }

  // Function expression with underscore, example:  func _
  def traverse(eta: Term.Eta): Unit = {
    traverse(eta.expr)
    // TODO - see if can be improved
    emit(s" $JavaPlaceholder")
  }

  // Passing vararg expression, in Java probably nothing to change (?)
  def traverse(termRepeated: Term.Repeated): Unit = {
    traverse(termRepeated.expr)
  }

  // method parameter declaration
  def traverse(termParam: Term.Param): Unit = {
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

  def traverse(termInterpolate: Term.Interpolate): Unit = {
    // Transform Scala string interpolation to Java String.format()
    termInterpolate.prefix match {
      case Term.Name("s") => traverse(toJavaStringFormatInvocation(termInterpolate.parts, termInterpolate.args))
      case _ => emitComment(s"UNPARSEABLE interpolation: $termInterpolate")
    }
  }

  def traverse(termXml: Term.Xml): Unit = {
    // TODO
    emitComment(termXml.toString())
  }


  def traverse(name: Type.Name): Unit = {
    emit(toJavaName(name))
  }

  // A scala type selecting expression like: a.B
  def traverse(typeSelect: Type.Select): Unit = {
    traverse(typeSelect.qual)
    emit(".")
    traverse(typeSelect.name)
  }

  // A scala type projecting expression like: a#B
  def traverse(typeProject: Type.Project): Unit = {
    traverse(typeProject.qual)
    emit(".")
    traverse(typeProject.name)
  }

  // A scala expression representing the type of a singleton like: A.type
  def traverse(singletonType: Type.Singleton): Unit = {
    traverse(singletonType.ref)
    emit(".class")
  }


  // type with generic args, e.g. F[T]
  def traverse(typeApply: Type.Apply): Unit = {
    traverse(typeApply.tpe)
    traverseGenericTypeList(typeApply.args)
  }

  // type with generic args in infix notation, e.g. K Map V
  def traverse(ignored: Type.ApplyInfix): Unit = {
    // TODO
  }

  // lambda type
  def traverse(functionType: Type.Function): Unit = {
    traverse(Type.Apply(Type.Name("Function"), functionType.params :+ functionType.res))
  }

  //tuple as type, cannot be translated directly into Java
  def traverse(tupleType: Type.Tuple): Unit = {
    emitComment(tupleType.toString())
  }

  // type with parent, e.g.  A with B
  // approximated by Java "extends" but might not compile
  def traverse(typeWith: Type.With): Unit = {
    traverse(typeWith.lhs)
    emit(" extends ")
    traverse(typeWith.rhs)
  }

  // A {def f: Int }
  def traverse(refinedType: Type.Refine): Unit = {
    refinedType.tpe.foreach(traverse)
    // TODO try to convert to Java type with inheritance
    emitComment(s" ${refinedType.stats.toString()}")
  }

  // type with existential constraint e.g.:  A[B] forSome {B <: Number with Serializable}
  def traverse(existentialType: Type.Existential): Unit = {
    traverse(existentialType.tpe)
    // TODO - convert to Java if there is one simple where clause
    emitComment(existentialType.stats.toString())
  }

  // type with annotation, e.g.: T @annot
  def traverse(annotatedType: Type.Annotate): Unit = {
    traverseAnnotations(annotatedType.annots)
    emit(" ")
    traverse(annotatedType.tpe)
  }

  // generic lambda type [T] => (T, T)
  // supported only in some dialects (?)
  def traverse(ignored: Type.Lambda): Unit = {
    // TODO
  }

  // _ in T[_]
  def traverse(placeholderType: Type.Placeholder): Unit = {
    emit("?")
    traverse(placeholderType.bounds)
  }

  // Scala type bounds e.g. T[X <: Y]
  def traverse(typeBounds: Type.Bounds): Unit = {
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
  def traverse(typeByName: Type.ByName): Unit = {
    // Java Consumer is the closest I can find
    traverse(Type.Apply(Type.Name("Consumer"), List(typeByName.tpe)))
  }

  // Vararg type,e.g.: T*
  def traverse(repeatedType: Type.Repeated): Unit = {
    traverse(repeatedType.tpe)
    emitEllipsis()
  }

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement
  def traverse(typeVar: Type.Var): Unit = {
    emitComment(typeVar.toString())
  }

  // Type param, e.g.: `T` in trait MyTrait[T]
  def traverse(typeParam: Type.Param): Unit = {
    // TODO handle mods
    traverse(typeParam.name)
    traverseGenericTypeList(typeParam.tparams)
    traverse(typeParam.tbounds)
    // TODO handle vbounds and cbounds (which aren't supported in Java, maybe partially ?)
  }


  def traverse(lit: Lit): Unit = {
    val strValue = lit.value match {
      case str: Lit.String => s"\"$str\""
      case Lit.Unit => ""
      case other => other.toString
    }
    emit(strValue)
  }

  // Wildcard in pattern match expression - translates to Java "default" ?
  def traverse(ignored: Pat.Wildcard): Unit = {
    emitComment("default")
  }

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*). Not translatable (?)
  def traverse(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    emitComment("_*")
  }

  // Pattern match variable, e.g. `a` in case a =>
  def traverse(patternVar: Pat.Var): Unit = {
    traverse(patternVar.name)
  }

  // Pattern match bind variable, e.g.: a @ A()
  def traverse(patternBind: Bind): Unit = {
    // In Java (when supported) the order is reversed
    traverse(patternBind.rhs)
    emit(" ")
    traverse(patternBind.lhs)
  }

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  def traverse(patternAlternative: Alternative): Unit = {
    traverse(patternAlternative.lhs)
    emit(", ")
    traverse(patternAlternative.rhs)
  }

  // Pattern match tuple expression, no Java equivalent
  def traverse(patternTuple: Pat.Tuple): Unit = {
    emitComment(s"(${patternTuple.args.toString()})")
  }

  // Pattern match extractor e.g. A(a, b).
  // No Java equivalent (but consider rewriting as a guard ?)
  def traverse(patternExtractor: Pat.Extract): Unit = {
    emitComment(s"${patternExtractor.fun}(${patternExtractor.args})")
  }

  // Pattern match extractor infix e.g. a E b.
  // No Java equivalent (but consider rewriting as a guard ?)
  def traverse(patternExtractorInfix: Pat.ExtractInfix): Unit = {
    emitComment(s"${patternExtractorInfix.lhs} ${patternExtractorInfix.op} ${patternExtractorInfix.rhs}")
  }

  // Pattern interpolation e.g. r"Hello (.+)$name" , no Java equivalent
  def traverse(patternInterpolation: Pat.Interpolate): Unit = {
    emitComment(patternInterpolation.toString())
  }

  // Pattern match xml
  def traverse(patternXml: Pat.Xml): Unit = {
    // TODO
    emitComment(patternXml.toString())
  }

  // Typed pattern expression. e.g. a: Int
  def traverse(typedPattern: Pat.Typed): Unit = {
    traverse(typedPattern.rhs)
    emit(" ")
    traverse(typedPattern.lhs)
  }


  def traverse(`case`: Case): Unit = {
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
  def traverse(anonymousName: Name.Anonymous): Unit = {
  }

  // Name that cannot be distinguished between a term and a type, so in Java we will just emit it unchanged (for example, in "import" statement)
  def traverse(indeterminateName: Name.Indeterminate): Unit = {
    emit(indeterminateName.value)
  }

  def traverse(`import`: Import): Unit = {
    `import`.importers match {
      case List() => emitComment("Invalid import with no inner importers")
      case importers => importers.foreach(traverse)
    }
  }

  def traverse(importer: Importer): Unit = {
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

  def traverse(importee: Importee): Unit = {
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

  def traverse(template: Template): Unit = {
    traverseTemplate(template, None, None)
  }

  def traverseTemplate(template: Template,
                               maybeExplicitPrimaryCtor: Option[Ctor.Primary] = None,
                               maybeClassName: Option[Type.Name] = None): Unit = {
    traverseTemplateInits(template.inits)
    template.self.decltpe.foreach(declType => {
      // TODO - consider translating the 'self' type into a Java parent
      emitComment(template.self.toString)
    })
    traverseTemplateBody(template.stats, maybeExplicitPrimaryCtor, maybeClassName)
  }

  def traverse(annotation: Annot): Unit = {
    emit("@")
    traverse(annotation.init)
  }

  def traverse(init: Init): Unit = {
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

  def traverseGenericTypeList(types: List[Tree]): Unit = {
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

  def traverseAnnotations(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.foreach(annotation => {
      traverse(annotation)
      if (onSameLine) {
        emit(" ")
      } else {
        emitLine()
      }
    })
  }

  def traverse(list: List[_ <: Tree], onSameLine: Boolean = false): Unit = {
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

  def traverseLastStatement(stmt: Stat): Unit = {
    emit("return ")
    traverse(stmt)
    emitStatementEnd()
  }

  private def toJavaName(typeName: Type.Name) = {
    ScalaTypeNameToJavaTypeName.getOrElse(typeName.value, typeName.value)
  }

  private def toJavaStringFormatInvocation(formatParts: List[Lit], interpolationArgs: List[Term]) = {
    Apply(Select(Term.Name("String"), Term.Name("format")), List(Lit.String(formatParts.mkString("%s"))) ++ interpolationArgs)
  }

  def resolveJavaClassExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Sealed], classOf[Final]))
    modifierNamesBuilder.result()
  }

  def resolveJavaInterfaceExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    modifierNamesBuilder += "public"
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods, List(classOf[Sealed]))
    modifierNamesBuilder.result()
  }

  def resolveJavaClassMethodExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods,
      List(classOf[Private], classOf[Protected], classOf[Abstract], classOf[Final]))
    modifierNamesBuilder.result()
  }

  def resolveJavaInterfaceMethodExplicitModifiers(mods: List[Mod], hasBody: Boolean): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && hasBody) {
      modifierNamesBuilder += "default"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods, List(classOf[Private]))
    modifierNamesBuilder.result()
  }

  def resolveJavaClassDataMemberExplicitModifiers(mods: List[Mod]): List[String] = {
    val modifierNamesBuilder = List.newBuilder[String]
    if (!mods.exists(_.isInstanceOf[Private]) && !mods.exists(_.isInstanceOf[Protected])) {
      modifierNamesBuilder += "public"
    }
    modifierNamesBuilder ++= resolveJavaExplicitModifiers(mods, List(classOf[Private], classOf[Protected], classOf[Final]))
    modifierNamesBuilder.result()
  }

  def resolveJavaExplicitModifiers(inputMods: List[Mod], allowedMods: List[Class[_ <: Mod]]): List[String] = {
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
