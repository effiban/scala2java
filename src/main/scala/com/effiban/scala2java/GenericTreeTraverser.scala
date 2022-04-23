package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._

import scala.meta.Defn.Trait
import scala.meta.Name.Indeterminate
import scala.meta.Pat.{Alternative, Bind}
import scala.meta.Term.{AnonymousFunction, ApplyType, ApplyUnary, Ascribe, Assign, Block, Do, Eta, For, ForYield, If, New, NewAnonymous, Return, Super, This, Throw, Try, TryWithHandler, While}
import scala.meta.{Case, Decl, Defn, Import, Importee, Importer, Init, Lit, Mod, Name, Pat, Pkg, Source, Template, Term, Tree, Type}

object GenericTreeTraverser extends ScalaTreeTraverser[Tree] {

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
    case typeRefine: Type.Refine => TypeRefineTraverser.traverse(typeRefine)
    case existentialType: Type.Existential => TypeExistentialTraverser.traverse(existentialType)
    case typeAnnotation: Type.Annotate => TypeAnnotateTraverser.traverse(typeAnnotation)
    case lambdaType: Type.Lambda => TypeLambdaTraverser.traverse(lambdaType)
    case placeholderType: Type.Placeholder => TypePlaceholderTraverser.traverse(placeholderType)
    case typeBounds: Type.Bounds => TypeBoundsTraverser.traverse(typeBounds)
    case byNameType: Type.ByName => TypeByNameTraverser.traverse(byNameType)
    case repeatedType: Type.Repeated => TypeRepeatedTraverser.traverse(repeatedType)
    case typeVar: Type.Var => TypeVarTraverser.traverse(typeVar)
    case typeParam: Type.Param => TypeParamTraverser.traverse(typeParam)

    case literal: Lit => LitTraverser.traverse(literal)
    case patternWildcard: Pat.Wildcard => PatWildcardTraverser.traverse(patternWildcard)
    case patternSeqWildcard: Pat.SeqWildcard => PatSeqWildcardTraverser.traverse(patternSeqWildcard)
    case patternVar: Pat.Var => PatVarTraverser.traverse(patternVar)
    case patternBind: Bind => BindTraverser.traverse(patternBind)
    case patternAlternative: Alternative => AlternativeTraverser.traverse(patternAlternative)
    case patternTuple: Pat.Tuple => PatTupleTraverser.traverse(patternTuple)
    case patternExtract: Pat.Extract => PatExtractTraverser.traverse(patternExtract)
    case patternExtractInfix: Pat.ExtractInfix => PatExtractInfixTraverser.traverse(patternExtractInfix)
    case patternInterpolate: Pat.Interpolate => PatInterpolateTraverser.traverse(patternInterpolate)
    case patternXml: Pat.Xml => PatXmlTraverser.traverse(patternXml)
    case patternTyped: Pat.Typed => PatTypedTraverser.traverse(patternTyped)

    case `case`: Case => CaseTraverser.traverse(`case`)

    case anonymousName: Name.Anonymous => NameAnonymousTraverser.traverse(anonymousName)
    case indeterminateName: Indeterminate => NameIndeterminateTraverser.traverse(indeterminateName)

    case template: Template => TemplateTraverser.traverse(template)

    case `import`: Import => ImportTraverser.traverse(`import`)
    case importer: Importer => ImporterTraverser.traverse(importer)
    case importee: Importee => ImporteeTraverser.traverse(importee)

    case annotation: Mod.Annot => AnnotTraverser.traverse(annotation)

    case init: Init => InitTraverser.traverse(init)

    case _ => emitComment(s"UNRECOGNIZED: $tree")
  }
}
