package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers._
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.TemplateChildContextFactory
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.resolvers._
import io.github.effiban.scala2java.core.transformers._
import io.github.effiban.scala2java.core.typeinference.TypeInferrers

class ScalaTreeTraversers(implicit typeInferrers: TypeInferrers,
                          extensionRegistry: ExtensionRegistry) {

  private implicit lazy val classifiers: Classifiers = new Classifiers(typeInferrers)
  private lazy val resolvers = new Resolvers()

  import resolvers._
  import typeInferrers._


  private lazy val alternativeTraverser: AlternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  private lazy val annotTraverser: AnnotTraverser = new AnnotTraverserImpl(initTraverser)

  private lazy val anonymousFunctionTraverser: AnonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  private lazy val applyTypeTraverser: ApplyTypeTraverser = new ApplyTypeTraverserImpl(expressionTermTraverser, typeTraverser)

  private lazy val applyUnaryTraverser: ApplyUnaryTraverser = new ApplyUnaryTraverserImpl(expressionTermTraverser)

  private lazy val arrayInitializerTraverser: ArrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    expressionTermTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  private lazy val ascribeTraverser: AscribeTraverser = new AscribeTraverserImpl(
    expressionTermTraverser,
    typeTraverser
  )

  private lazy val assignTraverser: AssignTraverser = new AssignTraverserImpl(expressionTermTraverser)

  private lazy val blockLastStatTraverser: BlockLastStatTraverser = new BlockLastStatTraverserImpl(
    blockStatTraverser,
    defaultIfTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    shouldReturnValueResolver
  )

  private lazy val blockStatTraverser: BlockStatTraverser = new BlockStatTraverserImpl(
    statTermTraverser,
    defnVarTraverser,
    declVarTraverser
  )

  private lazy val blockWrappingTermTraverser: BlockWrappingTermTraverser = new BlockWrappingTermTraverserImpl(defaultBlockTraverser)

  private lazy val caseClassTraverser: CaseClassTraverser = new CaseClassTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    termParamTraverser,
    templateTraverser
  )

  private lazy val caseTraverser: CaseTraverser = new CaseTraverserImpl(patTraverser, expressionTermTraverser)

  private lazy val catchArgumentTraverser: CatchArgumentTraverser = new CatchArgumentTraverserImpl(patTraverser)

  private lazy val catchHandlerTraverser: CatchHandlerTraverser = new CatchHandlerTraverserImpl(
    catchArgumentTraverser,
    blockWrappingTermTraverser
  )

  private lazy val classTraverser: ClassTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    new CompositeClassTransformer(),
    ClassClassifier
  )

  private lazy val ctorPrimaryTraverser: CtorPrimaryTraverser = new CtorPrimaryTraverserImpl(
    CtorPrimaryTransformer,
    defnDefTraverser
  )

  private lazy val ctorSecondaryTraverser: CtorSecondaryTraverser = new CtorSecondaryTraverserImpl(
    statModListTraverser,
    termParamTraverser,
    initTraverser,
    blockStatTraverser
  )

  private lazy val declDefTraverser: DeclDefTraverser = new DeclDefTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    typeTraverser,
    termParamTraverser
  )

  private lazy val declTraverser: DeclTraverser = new DeclTraverserImpl(declVarTraverser, declDefTraverser)

  private lazy val declVarTraverser: DeclVarTraverser = new DeclVarTraverserImpl(
    statModListTraverser,
    typeTraverser,
    patTraverser
  )

  private lazy val defaultBlockTraverser: DefaultBlockTraverser = new DefaultBlockTraverserImpl(
    blockStatTraverser,
    blockLastStatTraverser
  )

  private lazy val defaultIfTraverser: DefaultIfTraverser = new DefaultIfTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)

  private lazy val defaultStatTraverser: DefaultStatTraverser = new DefaultStatTraverserImpl(
    statTermTraverser,
    importTraverser,
    pkgTraverser,
    defnTraverser,
    declTraverser
  )

  private lazy val defaultTermRefTraverser: DefaultTermRefTraverser = new DefaultTermRefTraverserImpl(defaultTermSelectTraverser)

  private lazy val defaultTermSelectTraverser: DefaultTermSelectTraverser = new DefaultTermSelectTraverserImpl(defaultTermRefTraverser)

  private lazy val defaultTermTraverser: DefaultTermTraverser = new DefaultTermTraverserImpl(
    defaultTermRefTraverser,
    termApplyTraverser,
    applyTypeTraverser,
    termApplyInfixTraverser,
    assignTraverser,
    returnTraverser,
    throwTraverser,
    ascribeTraverser,
    termAnnotateTraverser,
    termTupleTraverser,
    defaultBlockTraverser,
    defaultIfTraverser,
    termMatchTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    termFunctionTraverser,
    partialFunctionTraverser,
    anonymousFunctionTraverser,
    whileTraverser,
    doTraverser,
    newTraverser,
    newAnonymousTraverser,
    etaTraverser,
    termRepeatedTraverser
  )

  private lazy val defnDefTraverser: DefnDefTraverser = new DefnDefTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    typeTraverser,
    termParamTraverser,
    blockWrappingTermTraverser,
    termTypeInferrer,
    new CompositeDefnDefTransformer()
  )

  private lazy val defnTraverser: DefnTraverser = new DefnTraverserImpl(
    defnVarTraverser,
    defnDefTraverser,
    traitTraverser,
    classTraverser,
    objectTraverser
  )

  private lazy val defnVarTypeTraverser: DefnVarTypeTraverser = new DefnVarTypeTraverserImpl(
    typeTraverser,
    termTypeInferrer
  )

  private lazy val defnVarTraverser: DefnVarTraverser = new DefnVarTraverserImpl(
    statModListTraverser,
    defnVarTypeTraverser,
    patTraverser,
    expressionTermTraverser,
    declVarTraverser,
    new CompositeDefnVarToDeclVarTransformer,
    new CompositeDefnVarTransformer
  )

  private lazy val doTraverser: DoTraverser = new DoTraverserImpl(blockWrappingTermTraverser, expressionTermTraverser)

  private lazy val etaTraverser: EtaTraverser = new EtaTraverserImpl(expressionTermTraverser)

  private lazy val expressionBlockTraverser: ExpressionBlockTraverser = new ExpressionBlockTraverserImpl(expressionTermTraverser)

  private lazy val expressionIfTraverser: ExpressionIfTraverser = new ExpressionIfTraverserImpl(expressionTermTraverser)

  private lazy val expressionTermRefTraverser: ExpressionTermRefTraverser = new ExpressionTermRefTraverserImpl(
    expressionTermSelectTraverser,
    applyUnaryTraverser,
    defaultTermRefTraverser
  )

  private lazy val expressionTermSelectTraverser: ExpressionTermSelectTraverser = new ExpressionTermSelectTraverserImpl(expressionTermTraverser)

  private lazy val expressionTermTraverser: ExpressionTermTraverser = new ExpressionTermTraverserImpl(
    expressionTermRefTraverser,
    expressionBlockTraverser,
    expressionIfTraverser,
    defaultTermTraverser
  )

  private lazy val finallyTraverser: FinallyTraverser = new FinallyTraverserImpl(blockWrappingTermTraverser)

  private lazy val importerTraverser: ImporterTraverser = new ImporterTraverserImpl(defaultTermRefTraverser)

  private lazy val importTraverser: ImportTraverser = new ImportTraverserImpl(importerTraverser)

  private lazy val initTraverser: InitTraverser = new InitTraverserImpl(typeTraverser, expressionTermTraverser)

  private lazy val newAnonymousTraverser: NewAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  private lazy val newTraverser: NewTraverser = new NewTraverserImpl(
    initTraverser,
    arrayInitializerTraverser,
    ArrayInitializerContextResolver
  )

  private lazy val objectTraverser: ObjectTraverser = new ObjectTraverserImpl(
    statModListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val partialFunctionTraverser: PartialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  private lazy val patTraverser: PatTraverser = new PatTraverserImpl(
    PatSeqWildcardTraverser,
    BindTraverser,
    alternativeTraverser,
    PatTupleTraverser,
    PatExtractTraverser,
    PatExtractInfixTraverser,
    PatInterpolateTraverser,
    patTypedTraverser
  )

  private lazy val patTypedTraverser: PatTypedTraverser = new PatTypedTraverserImpl(patTraverser, typeTraverser)

  private lazy val pkgStatTraverser: PkgStatTraverser = new PkgStatTraverserImpl(
    classTraverser,
    traitTraverser,
    objectTraverser,
    defaultStatTraverser
  )

  private lazy val pkgStatListTraverser: PkgStatListTraverser = new PkgStatListTraverserImpl(pkgStatTraverser)

  private lazy val pkgTraverser: PkgTraverser = new PkgTraverserImpl(
    defaultTermRefTraverser,
    pkgStatListTraverser
  )

  private lazy val regularClassTraverser: RegularClassTraverser = new RegularClassTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    templateTraverser,
    ParamToDeclVarTransformer,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val returnTraverser: ReturnTraverser = new ReturnTraverserImpl(expressionTermTraverser)

  private lazy val selfTraverser: SelfTraverser = new SelfTraverserImpl(typeTraverser)

  lazy val sourceTraverser: SourceTraverser = new SourceTraverserImpl(defaultStatTraverser)

  private lazy val statModListTraverser: StatModListTraverser = new StatModListTraverserImpl(annotTraverser)

  private lazy val statTermTraverser: StatTermTraverser = new StatTermTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser
  )

  private lazy val templateBodyTraverser: TemplateBodyTraverser = new TemplateBodyTraverserImpl(
    templateChildrenTraverser,
    new TemplateStatTransformerImpl(new CompositeTemplateTermApplyInfixToDefnTransformer, new CompositeTemplateTermApplyToDefnTransformer),
    TemplateChildrenResolver,
    TemplateChildContextFactory
  )

  private lazy val templateChildrenTraverser: TemplateChildrenTraverser = new TemplateChildrenTraverserImpl(
    templateChildTraverser,
    JavaTemplateChildOrdering
  )

  private lazy val templateChildTraverser: TemplateChildTraverser = new TemplateChildTraverserImpl(
    ctorPrimaryTraverser,
    ctorSecondaryTraverser,
    defaultStatTraverser,
    TraitClassifier
  )

  private lazy val templateInitTraverser: TemplateInitTraverser = new TemplateInitTraverserImpl(typeTraverser)

  private lazy val templateTraverser: TemplateTraverser = new TemplateTraverserImpl(
    templateInitTraverser,
    selfTraverser,
    templateBodyTraverser
  )

  private lazy val termAnnotateTraverser: TermAnnotateTraverser = new TermAnnotateTraverserImpl(expressionTermTraverser, annotTraverser)

  private lazy val termApplyInfixTraverser: TermApplyInfixTraverser = new TermApplyInfixTraverserImpl(expressionTermTraverser)

  private lazy val termApplyTraverser: TermApplyTraverser = new TermApplyTraverserImpl(
    expressionTermTraverser,
    arrayInitializerTraverser,
    ArrayInitializerContextResolver
  )

  private lazy val termFunctionTraverser: TermFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    defaultBlockTraverser,
    defaultTermTraverser
  )

  private lazy val termMatchTraverser: TermMatchTraverser = new TermMatchTraverserImpl(expressionTermTraverser, caseTraverser)

  private lazy val termParamModListTraverser: TermParamModListTraverser =
    new TermParamModListTraverserImpl(annotTraverser, JavaFinalModifierResolver)

  private lazy val termParamTraverser: TermParamTraverser = new TermParamTraverserImpl(
    termParamModListTraverser,
    typeTraverser,
    expressionTermTraverser
  )

  private lazy val termRepeatedTraverser: TermRepeatedTraverser = new TermRepeatedTraverserImpl(expressionTermTraverser)

  private lazy val termTupleTraverser: TermTupleTraverser = new TermTupleTraverserImpl(expressionTermTraverser)

  private lazy val throwTraverser: ThrowTraverser = new ThrowTraverserImpl(expressionTermTraverser)

  private lazy val traitTraverser: TraitTraverser = new TraitTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    templateTraverser
  )

  private lazy val tryTraverser: TryTraverser = new TryTraverserImpl(
    blockWrappingTermTraverser,
    catchHandlerTraverser,
    finallyTraverser
  )

  private lazy val tryWithHandlerTraverser: TryWithHandlerTraverser = new TryWithHandlerTraverserImpl(blockWrappingTermTraverser, finallyTraverser)

  private lazy val typeAnnotateTraverser: TypeAnnotateTraverser = new TypeAnnotateTraverserImpl(typeTraverser)

  private lazy val typeApplyInfixTraverser: TypeApplyInfixTraverser = new TypeApplyInfixTraverserImpl()

  private lazy val typeApplyTraverser: TypeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser)

  private lazy val typeBoundsTraverser: TypeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  private lazy val typeByNameTraverser: TypeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, TypeByNameToSupplierTypeTransformer)

  private lazy val typeExistentialTraverser: TypeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  private lazy val typeFunctionTraverser: TypeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser)

  private lazy val typeParamTraverser: TypeParamTraverser = new TypeParamTraverserImpl(typeBoundsTraverser)

  private lazy val typeProjectTraverser: TypeProjectTraverser = new TypeProjectTraverserImpl(typeTraverser)

  private lazy val typeRefineTraverser: TypeRefineTraverser = new TypeRefineTraverserImpl(typeTraverser)

  private lazy val typeRefTraverser: TypeRefTraverser = new TypeRefTraverserImpl(
    typeSelectTraverser,
    typeProjectTraverser
  )

  private lazy val typeRepeatedTraverser: TypeRepeatedTraverser = new TypeRepeatedTraverserImpl(typeTraverser)

  private lazy val typeSelectTraverser: TypeSelectTraverser = new TypeSelectTraverserImpl(defaultTermRefTraverser)

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
    typeWildcardTraverser,
    typeByNameTraverser,
    typeRepeatedTraverser
  )

  private lazy val typeTupleTraverser: TypeTupleTraverser = new TypeTupleTraverserImpl(typeTraverser)

  private lazy val typeWildcardTraverser: TypeWildcardTraverser = new TypeWildcardTraverserImpl(typeBoundsTraverser)

  private lazy val typeWithTraverser: TypeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  private lazy val whileTraverser: WhileTraverser = new WhileTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)
}
