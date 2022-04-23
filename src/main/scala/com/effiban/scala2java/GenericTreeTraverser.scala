package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn.Trait
import scala.meta.Mod.{Abstract, Annot, Final, Private, Protected, Sealed}
import scala.meta.Name.Indeterminate
import scala.meta.Pat.{Alternative, Bind}
import scala.meta.Term.{AnonymousFunction, ApplyType, ApplyUnary, Ascribe, Assign, Block, Do, Eta, For, ForYield, If, New, NewAnonymous, Return, Select, Super, This, Throw, Try, TryWithHandler, While}
import scala.meta.{Case, Ctor, Decl, Defn, Import, Importee, Importer, Init, Lit, Mod, Name, Pat, Pkg, Source, Stat, Template, Term, Tree, Type}

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
    case ascribe: Ascribe => AscribeTraverser.traverse(ascribe)
    case annotate: Term.Annotate => TermAnnotateTraverser.traverse(annotate)
    case tuple: Term.Tuple => TermTupleTraverser.traverse(tuple)
    case block: Block => BlockTraverser.traverse(block)
    case `if`: If => IfTraverser.traverse(`if`)
    case `match`: Term.Match => TermMatchTraverser.traverse(`match`)
    case `try`: Try => TryTraverser.traverse(`try`)
    case tryWithHandler: TryWithHandler => TryWithHandlerTraverser.traverse(tryWithHandler)
    case `function`: Term.Function => TermFunctionTraverser.traverse(`function`)
    case partialFunction: Term.PartialFunction => PartialFunctionTraverser.traverse(partialFunction)
    case anonFunction: AnonymousFunction => AnonymousFunctionTraverser.traverse(anonFunction)
    case `while`: While => WhileTraverser.traverse(`while`)
    case `do`: Do => DoTraverser.traverse(`do`)
    case `for`: For => ForTraverser.traverse(`for`)
    case forYield: ForYield => ForYieldTraverser.traverse(forYield)
    case `new`: New => NewTraverser.traverse(`new`)
    case newAnonymous: NewAnonymous => NewAnonymousTraverser.traverse(newAnonymous)
    case termPlaceholder: Term.Placeholder => TermPlaceholderTraverser.traverse(termPlaceholder)
    case eta: Eta => EtaTraverser.traverse(eta)
    case termRepeated: Term.Repeated => TermRepeatedTraverser.traverse(termRepeated)
    case termParam: Term.Param => TermParamTraverser.traverse(termParam)
    case interpolate: Term.Interpolate => TermInterpolateTraverser.traverse(interpolate)
    case xml: Term.Xml => TermXmlTraverser.traverse(xml)

    case typeName: Type.Name => TypeNameTraverser.traverse(typeName)
    case typeSelect: Type.Select => TypeSelectTraverser.traverse(typeSelect)
    case typeProject: Type.Project => TypeProjectTraverser.traverse(typeProject)
    case typeSingleton: Type.Singleton => TypeSingletonTraverser.traverse(typeSingleton)

    case typeApply: Type.Apply => TypeApplyTraverser.traverse(typeApply)
    case typeApplyInfix: Type.ApplyInfix => TypeApplyInfixTraverser.traverse(typeApplyInfix)
    case functionType: Type.Function => TypeFunctionTraverser.traverse(functionType)
    case tupleType: Type.Tuple => TypeTupleTraverser.traverse(tupleType)
    case withType: Type.With => TypeWithTraverser.traverse(withType)
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
}
