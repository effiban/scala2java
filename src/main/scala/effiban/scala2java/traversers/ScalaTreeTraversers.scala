package effiban.scala2java.traversers

import effiban.scala2java.classifiers._
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.resolvers.Resolvers.shouldReturnValueResolver
import effiban.scala2java.resolvers._
import effiban.scala2java.transformers._
import effiban.scala2java.typeinference.TypeInferrers.termTypeInferrer
import effiban.scala2java.writers.JavaWriter

class ScalaTreeTraversers(implicit javaWriter: JavaWriter) {

  private lazy val alternativeTraverser: AlternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  private lazy val annotListTraverser: AnnotListTraverser = new AnnotListTraverserImpl(annotTraverser)

  private lazy val annotTraverser: AnnotTraverser = new AnnotTraverserImpl(initTraverser)

  private lazy val anonymousFunctionTraverser: AnonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  private lazy val applyTypeTraverser: ApplyTypeTraverser = new ApplyTypeTraverserImpl(
    typeTraverser,
    termSelectTraverser,
    termTraverser
  )

  private lazy val applyUnaryTraverser: ApplyUnaryTraverser = new ApplyUnaryTraverserImpl(termNameTraverser, termTraverser)

  private lazy val argumentListTraverser: ArgumentListTraverser = new ArgumentListTraverserImpl

  private lazy val ascribeTraverser: AscribeTraverser = new AscribeTraverserImpl(typeTraverser, termTraverser)

  private lazy val assignTraverser: AssignTraverser = new AssignTraverserImpl(termTraverser, rhsTermTraverser)

  private lazy val bindTraverser: BindTraverser = new BindTraverserImpl(patTraverser)

  private lazy val blockStatTraverser: BlockStatTraverser = new BlockStatTraverserImpl(
    ifTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    statTraverser,
    shouldReturnValueResolver,
    JavaStatClassifier
  )

  private lazy val blockTraverser: BlockTraverser = new BlockTraverserImpl(
    initTraverser,
    blockStatTraverser,
  )

  private lazy val caseClassTraverser: CaseClassTraverser = new CaseClassTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    termParamListTraverser,
    templateTraverser,
    JavaModifiersResolver,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val caseTraverser: CaseTraverser = new CaseTraverserImpl(patTraverser, termTraverser)

  private lazy val catchHandlerTraverser: CatchHandlerTraverser = new CatchHandlerTraverserImpl(termParamListTraverser, blockTraverser)

  private lazy val classTraverser: ClassTraverser = new ClassTraverserImpl(caseClassTraverser, regularClassTraverser)

  private lazy val ctorPrimaryTraverser: CtorPrimaryTraverser = new CtorPrimaryTraverserImpl(CtorPrimaryTransformer, defnDefTraverser)

  private lazy val ctorSecondaryTraverser: CtorSecondaryTraverser = new CtorSecondaryTraverserImpl(CtorSecondaryTransformer, defnDefTraverser)

  private lazy val declDefTraverser: DeclDefTraverser = new DeclDefTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    typeTraverser,
    termNameTraverser,
    termParamListTraverser,
    JavaModifiersResolver
  )

  private lazy val declTraverser: DeclTraverser = new DeclTraverserImpl(
    declValTraverser,
    declVarTraverser,
    declDefTraverser,
    declTypeTraverser)

  private lazy val declTypeTraverser: DeclTypeTraverser = new DeclTypeTraverserImpl(
    typeParamListTraverser,
    JavaModifiersResolver,
    JavaTreeTypeResolver)

  private lazy val declValTraverser: DeclValTraverser = new DeclValTraverserImpl(
    annotListTraverser,
    typeTraverser,
    patListTraverser,
    JavaModifiersResolver
  )

  private lazy val declVarTraverser: DeclVarTraverser = new DeclVarTraverserImpl(
    annotListTraverser,
    typeTraverser,
    patListTraverser,
    JavaModifiersResolver
  )

  private lazy val defnDefTraverser: DefnDefTraverser = new DefnDefTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    termNameTraverser,
    typeTraverser,
    termParamListTraverser,
    blockTraverser,
    termTypeInferrer,
    JavaModifiersResolver
  )

  private lazy val defnTraverser: DefnTraverser = new DefnTraverserImpl(
    defnValTraverser,
    defnVarTraverser,
    defnDefTraverser,
    defnTypeTraverser,
    classTraverser,
    traitTraverser,
    objectTraverser
  )

  private lazy val defnTypeTraverser: DefnTypeTraverser = new DefnTypeTraverserImpl(
    typeParamListTraverser,
    typeTraverser,
    typeBoundsTraverser,
    JavaModifiersResolver,
    JavaTreeTypeResolver
  )

  private lazy val defnValOrVarTypeTraverser: DefnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(
    typeTraverser,
    termTypeInferrer
  )

  private lazy val defnValTraverser: DefnValTraverser = new DefnValTraverserImpl(
    annotListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    rhsTermTraverser,
    JavaModifiersResolver
  )

  private lazy val defnVarTraverser: DefnVarTraverser = new DefnVarTraverserImpl(
    annotListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    rhsTermTraverser,
    JavaModifiersResolver
  )

  private lazy val doTraverser: DoTraverser = new DoTraverserImpl(termTraverser, blockTraverser)

  private lazy val enumConstantListTraverser: EnumConstantListTraverser = new EnumConstantListTraverserImpl(argumentListTraverser)

  private lazy val etaTraverser: EtaTraverser = new EtaTraverserImpl(termTraverser)

  private lazy val finallyTraverser: FinallyTraverser = new FinallyTraverserImpl(blockTraverser)

  private lazy val forTraverser: ForTraverser = new ForTraverserImpl(termApplyTraverser, ForToTermApplyTransformer)

  private lazy val forYieldTraverser: ForYieldTraverser = new ForYieldTraverserImpl(termApplyTraverser, ForYieldToTermApplyTransformer)

  private lazy val ifTraverser: IfTraverser = new IfTraverserImpl(termTraverser, blockTraverser)

  private lazy val importeeTraverser: ImporteeTraverser = new ImporteeTraverserImpl(nameTraverser)

  private lazy val importerTraverser: ImporterTraverser = new ImporterTraverserImpl(termRefTraverser, importeeTraverser)

  private lazy val importTraverser: ImportTraverser = new ImportTraverserImpl(importerTraverser, ImporterClassifier)

  private lazy val initListTraverser: InitListTraverser = new InitListTraverserImpl(argumentListTraverser, initTraverser)

  private lazy val initTraverser: InitTraverser = new InitTraverserImpl(typeTraverser, invocationArgListTraverser)

  private lazy val invocationArgListTraverser: InvocationArgListTraverser = new InvocationArgListTraverserImpl(
    argumentListTraverser,
    invocationArgTraverser
  )

  private lazy val invocationArgTraverser: InvocationArgTraverser = new InvocationArgTraverserImpl(assignTraverser, termTraverser)

  private lazy val litTraverser: LitTraverser = new LitTraverserImpl()

  private lazy val nameIndeterminateTraverser: NameIndeterminateTraverser = new NameIndeterminateTraverserImpl()

  private lazy val nameTraverser: NameTraverser = new NameTraverserImpl(
    NameAnonymousTraverser,
    nameIndeterminateTraverser,
    termNameTraverser,
    typeNameTraverser
  )

  private lazy val newAnonymousTraverser: NewAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  private lazy val newTraverser: NewTraverser = new NewTraverserImpl(initTraverser)

  private lazy val objectTraverser: ObjectTraverser = new ObjectTraverserImpl(
    annotListTraverser,
    templateTraverser,
    JavaModifiersResolver,
    JavaTreeTypeResolver,
    JavaChildScopeResolver)

  private lazy val partialFunctionTraverser: PartialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  private lazy val patExtractInfixTraverser: PatExtractInfixTraverser = new PatExtractInfixTraverserImpl(patExtractTraverser)

  private lazy val patExtractTraverser: PatExtractTraverser = new PatExtractTraverserImpl()

  private lazy val patInterpolateTraverser: PatInterpolateTraverser = new PatInterpolateTraverserImpl()

  private lazy val patListTraverser: PatListTraverser = new PatListTraverserImpl(argumentListTraverser, patTraverser)

  private lazy val patSeqWildcardTraverser: PatSeqWildcardTraverser = new PatSeqWildcardTraverserImpl()

  private lazy val patTraverser: PatTraverser = new PatTraverserImpl(
    litTraverser,
    termNameTraverser,
    patWildcardTraverser,
    patSeqWildcardTraverser,
    patVarTraverser,
    bindTraverser,
    alternativeTraverser,
    patTupleTraverser,
    patExtractTraverser,
    patExtractInfixTraverser,
    patInterpolateTraverser,
    patTypedTraverser
  )

  private lazy val patTupleTraverser: PatTupleTraverser = new PatTupleTraverserImpl()

  private lazy val patTypedTraverser: PatTypedTraverser = new PatTypedTraverserImpl(typeTraverser, patTraverser)

  private lazy val patVarTraverser: PatVarTraverser = new PatVarTraverserImpl(termNameTraverser)

  private lazy val patWildcardTraverser: PatWildcardTraverser = new PatWildcardTraverserImpl()

  private lazy val permittedSubTypeNameListTraverser = new PermittedSubTypeNameListTraverserImpl(argumentListTraverser)

  private lazy val pkgStatTraverser: PkgStatTraverser = new PkgStatTraverserImpl(
    classTraverser,
    traitTraverser,
    objectTraverser,
    statTraverser
  )

  private lazy val pkgStatListTraverser: PkgStatListTraverser = new PkgStatListTraverserImpl(
    pkgStatTraverser,
    SealedHierarchiesResolver
  )

  private lazy val pkgTraverser: PkgTraverser = new PkgTraverserImpl(termRefTraverser, pkgStatListTraverser)

  private lazy val regularClassTraverser: RegularClassTraverser = new RegularClassTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    templateTraverser,
    ParamToDeclValTransformer,
    JavaModifiersResolver,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val returnTraverser: ReturnTraverser = new ReturnTraverserImpl(termTraverser)

  private lazy val rhsTermTraverser: RhsTermTraverser = new RhsTermTraverserImpl(ifTraverser, termTraverser)

  private lazy val selfTraverser: SelfTraverser = new SelfTraverserImpl

  lazy val sourceTraverser: SourceTraverser = new SourceTraverserImpl(statTraverser)

  private lazy val statTraverser: StatTraverser = new StatTraverserImpl(
    termTraverser,
    importTraverser,
    pkgTraverser,
    defnTraverser,
    declTraverser
  )

  private lazy val superTraverser: SuperTraverser = new SuperTraverserImpl(nameTraverser)

  private lazy val templateChildTraverser: TemplateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    enumConstantListTraverser,
    statTraverser,
    DefnValClassifier,
    JavaStatClassifier
  )

  private lazy val templateBodyTraverser: TemplateBodyTraverser = new TemplateBodyTraverserImpl(
    templateChildTraverser,
    DefnTypeClassifier,
    JavaTemplateChildOrdering
  )

  private lazy val templateTraverser: TemplateTraverser = new TemplateTraverserImpl(
    initListTraverser,
    selfTraverser,
    templateBodyTraverser,
    permittedSubTypeNameListTraverser,
    JavaInheritanceKeywordResolver
  )

  private lazy val termAnnotateTraverser: TermAnnotateTraverser = new TermAnnotateTraverserImpl(annotListTraverser, termTraverser)

  private lazy val termApplyInfixTraverser: TermApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    termTraverser,
    termApplyTraverser,
    termNameTraverser,
    invocationArgListTraverser,
    TermApplyInfixClassifier,
    TermApplyInfixToRangeTransformer,
    TermApplyInfixToMapEntryTransformer
  )

  private lazy val termApplyTraverser: TermApplyTraverser = new TermApplyTraverserImpl(
    termTraverser,
    invocationArgListTraverser,
    TermApplyTransformer
  )

  private lazy val termFunctionTraverser: TermFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    termParamListTraverser,
    statTraverser,
    blockTraverser
  )

  private lazy val termInterpolateTraverser: TermInterpolateTraverser = new TermInterpolateTraverserImpl(TermInterpolateTransformer, termApplyTraverser)

  private lazy val termMatchTraverser: TermMatchTraverser = new TermMatchTraverserImpl(termTraverser, caseTraverser)

  private lazy val termNameTraverser: TermNameTraverser = new TermNameTraverserImpl

  private lazy val termParamListTraverser: TermParamListTraverser = new TermParamListTraverserImpl(argumentListTraverser, termParamTraverser)

  private lazy val termParamTraverser: TermParamTraverser = new TermParamTraverserImpl(
    annotListTraverser,
    typeTraverser,
    nameTraverser,
    JavaModifiersResolver
  )

  private lazy val termPlaceholderTraverser: TermPlaceholderTraverser = new TermPlaceholderTraverserImpl

  private lazy val termRefTraverser: TermRefTraverser = new TermRefTraverserImpl(
    thisTraverser,
    superTraverser,
    termNameTraverser,
    termSelectTraverser,
    applyUnaryTraverser
  )

  private lazy val termRepeatedTraverser: TermRepeatedTraverser = new TermRepeatedTraverserImpl(termTraverser)

  private lazy val termSelectTraverser: TermSelectTraverser = new TermSelectTraverserImpl(
    termTraverser,
    termNameTraverser,
    typeListTraverser,
    TermSelectTransformer
  )

  private lazy val termTraverser: TermTraverser = new TermTraverserImpl(
    termRefTraverser,
    termApplyTraverser,
    applyTypeTraverser,
    termApplyInfixTraverser,
    assignTraverser,
    returnTraverser,
    throwTraverser,
    ascribeTraverser,
    termAnnotateTraverser,
    termTupleTraverser,
    blockTraverser,
    ifTraverser,
    termMatchTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    termFunctionTraverser,
    partialFunctionTraverser,
    anonymousFunctionTraverser,
    whileTraverser,
    doTraverser,
    forTraverser,
    forYieldTraverser,
    newTraverser,
    newAnonymousTraverser,
    termPlaceholderTraverser,
    etaTraverser,
    termRepeatedTraverser,
    termInterpolateTraverser,
    litTraverser
  )

  private lazy val termTupleTraverser: TermTupleTraverser = new TermTupleTraverserImpl(
    termApplyTraverser,
    TermTupleToTermApplyTransformer
  )

  private lazy val thisTraverser: ThisTraverser = new ThisTraverserImpl(nameTraverser)

  private lazy val throwTraverser: ThrowTraverser = new ThrowTraverserImpl(termTraverser)

  private lazy val traitTraverser: TraitTraverser = new TraitTraverserImpl(
    annotListTraverser,
    typeParamListTraverser,
    templateTraverser,
    JavaModifiersResolver,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val tryTraverser: TryTraverser = new TryTraverserImpl(
    blockTraverser,
    catchHandlerTraverser,
    finallyTraverser,
    PatToTermParamTransformer
  )

  private lazy val tryWithHandlerTraverser: TryWithHandlerTraverser = new TryWithHandlerTraverserImpl(blockTraverser, finallyTraverser)

  private lazy val typeAnnotateTraverser: TypeAnnotateTraverser = new TypeAnnotateTraverserImpl(annotListTraverser, typeTraverser)

  private lazy val typeApplyInfixTraverser: TypeApplyInfixTraverser = new TypeApplyInfixTraverserImpl

  private lazy val typeApplyTraverser: TypeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser, typeListTraverser)

  private lazy val typeBoundsTraverser: TypeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  private lazy val typeByNameTraverser: TypeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, TypeByNameToSupplierTypeTransformer)

  private lazy val typeExistentialTraverser: TypeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  private lazy val typeFunctionTraverser: TypeFunctionTraverser = new TypeFunctionTraverserImpl(typeApplyTraverser, FunctionTypeTransformer)

  private lazy val typeLambdaTraverser: TypeLambdaTraverser = new TypeLambdaTraverserImpl

  private lazy val typeListTraverser: TypeListTraverser = new TypeListTraverserImpl(argumentListTraverser, typeTraverser)

  private lazy val typeNameTraverser: TypeNameTraverser = new TypeNameTraverserImpl(TypeNameTransformer)

  private lazy val typeParamListTraverser: TypeParamListTraverser = new TypeParamListTraverserImpl(argumentListTraverser, typeParamTraverser)

  private lazy val typeParamTraverser: TypeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    typeParamListTraverser,
    typeBoundsTraverser
  )

  private lazy val typePlaceholderTraverser: TypePlaceholderTraverser = new TypePlaceholderTraverserImpl(typeBoundsTraverser)

  private lazy val typeProjectTraverser: TypeProjectTraverser = new TypeProjectTraverserImpl(typeTraverser, typeNameTraverser)

  private lazy val typeRefineTraverser: TypeRefineTraverser = new TypeRefineTraverserImpl(typeTraverser)

  private lazy val typeRefTraverser: TypeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeSelectTraverser,
    typeProjectTraverser,
    typeSingletonTraverser
  )

  private lazy val typeRepeatedTraverser: TypeRepeatedTraverser = new TypeRepeatedTraverserImpl(typeTraverser)

  private lazy val typeSelectTraverser: TypeSelectTraverser = new TypeSelectTraverserImpl(termRefTraverser, typeNameTraverser)

  private lazy val typeSingletonTraverser: TypeSingletonTraverser = new TypeSingletonTraverserImpl(termTraverser, TypeSingletonToTermTransformer)

  private lazy val typeTraverser: TypeTraverser = new TypeTraverserImpl(
    typeRefTraverser,
    typeApplyTraverser,
    typeApplyInfixTraverser,
    typeFunctionTraverser,
    typeTupleTraverser,
    typeWithTraverser,
    typeRefineTraverser,
    typeExistentialTraverser,
    typeAnnotateTraverser,
    typeLambdaTraverser,
    typePlaceholderTraverser,
    typeByNameTraverser,
    typeRepeatedTraverser,
    typeVarTraverser
  )

  private lazy val typeTupleTraverser: TypeTupleTraverser = new TypeTupleTraverserImpl

  private lazy val typeVarTraverser: TypeVarTraverser = new TypeVarTraverserImpl

  private lazy val typeWithTraverser: TypeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  private lazy val whileTraverser: WhileTraverser = new WhileTraverserImpl(termTraverser, blockTraverser)
}
