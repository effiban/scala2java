package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{Classifiers, DefnValClassifier, JavaStatClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.TemplateChildContextFactory
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.predicates._
import io.github.effiban.scala2java.core.providers.{CompositeAdditionalImportersProvider, CoreAdditionalImportersProvider}
import io.github.effiban.scala2java.core.resolvers._
import io.github.effiban.scala2java.core.transformers._
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Assign

class ScalaTreeTraversers(implicit javaWriter: JavaWriter, extensionRegistry: ExtensionRegistry) {

  private implicit lazy val typeInferrers: TypeInferrers = new TypeInferrers()
  private implicit lazy val classifiers: Classifiers = new Classifiers(typeInferrers)
  private implicit lazy val transformers: Transformers = new Transformers(typeInferrers)
  private lazy val resolvers = new Resolvers()

  import resolvers._
  import transformers._
  import typeInferrers._


  private lazy val alternativeTraverser: AlternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  private lazy val annotListTraverser: AnnotListTraverser = new AnnotListTraverserImpl(annotTraverser)

  private lazy val annotTraverser: AnnotTraverser = new AnnotTraverserImpl(initTraverser)

  private lazy val anonymousFunctionTraverser: AnonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  private lazy val applyTypeTraverser: ApplyTypeTraverser = new ApplyTypeTraverserImpl(
    typeTraverser,
    termSelectTraverser,
    typeListTraverser,
    termTraverser,
    termApplyTraverser,
    new CompositeTermApplyTypeToTermApplyTransformer()
  )

  private lazy val applyUnaryTraverser: ApplyUnaryTraverser = new ApplyUnaryTraverserImpl(termNameTraverser, expressionTraverser)

  private lazy val argumentListTraverser: ArgumentListTraverser = new ArgumentListTraverserImpl

  private lazy val arrayInitializerTraverser: ArrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    expressionTraverser,
    new SimpleArgumentTraverser(termTraverser),
    argumentListTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  private lazy val ascribeTraverser: AscribeTraverser = new AscribeTraverserImpl(typeTraverser, expressionTraverser)

  private lazy val assignInvocationArgTraverser: InvocationArgTraverser[Assign] = new AssignInvocationArgTraverser(
    assignLHSTraverser,
    defaultInvocationArgTraverser
  )

  private lazy val assignLHSTraverser: AssignLHSTraverser = new AssignLHSTraverserImpl(termTraverser)

  private lazy val assignTraverser: AssignTraverser = new AssignTraverserImpl(assignLHSTraverser, expressionTraverser)

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
    modListTraverser,
    typeParamListTraverser,
    termParamListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val caseTraverser: CaseTraverser = new CaseTraverserImpl(
    patTraverser,
    expressionTraverser,
    termTraverser
  )

  private lazy val catchHandlerTraverser: CatchHandlerTraverser = new CatchHandlerTraverserImpl(termParamListTraverser, blockTraverser)

  private lazy val classTraverser: ClassTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    new CompositeClassTransformer(),
  )

  private lazy val compositeInvocationArgTraverser: InvocationArgTraverser[Term] = new CompositeInvocationArgTraverser(
    assignInvocationArgTraverser,
    defaultInvocationArgTraverser
  )

  private lazy val ctorPrimaryTraverser: CtorPrimaryTraverser = new CtorPrimaryTraverserImpl(CtorPrimaryTransformer, defnDefTraverser)

  private lazy val ctorSecondaryTraverser: CtorSecondaryTraverser = new CtorSecondaryTraverserImpl(CtorSecondaryTransformer, defnDefTraverser)

  private lazy val declDefTraverser: DeclDefTraverser = new DeclDefTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    typeTraverser,
    termNameTraverser,
    termParamListTraverser,
  )

  private lazy val declTraverser: DeclTraverser = new DeclTraverserImpl(
    declValTraverser,
    declVarTraverser,
    declDefTraverser,
    declTypeTraverser)

  private lazy val declTypeTraverser: DeclTypeTraverser = new DeclTypeTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    JavaTreeTypeResolver)

  private lazy val declValTraverser: DeclValTraverser = new DeclValTraverserImpl(
    modListTraverser,
    typeTraverser,
    patListTraverser
  )

  private lazy val declVarTraverser: DeclVarTraverser = new DeclVarTraverserImpl(
    modListTraverser,
    typeTraverser,
    patListTraverser
  )

  private lazy val defaultInvocationArgTraverser: InvocationArgTraverser[Term] = new DefaultInvocationArgTraverser(
    expressionTraverser,
    new CompositeInvocationArgByNamePredicate(CoreInvocationArgByNamePredicate)
  )

  private lazy val defnDefTraverser: DefnDefTraverser = new DefnDefTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    termNameTraverser,
    typeTraverser,
    termParamListTraverser,
    blockTraverser,
    termTypeInferrer,
    new CompositeDefnDefTransformer()
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
    modListTraverser,
    typeParamListTraverser,
    typeTraverser,
    typeBoundsTraverser,
    JavaTreeTypeResolver
  )

  private lazy val defnValOrVarTypeTraverser: DefnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(
    typeTraverser,
    termTypeInferrer
  )

  private lazy val defnValTraverser: DefnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    expressionTraverser,
    declVarTraverser,
    new CompositeDefnValToDeclVarTransformer,
    new CompositeDefnValTransformer
  )

  private lazy val defnVarTraverser: DefnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    expressionTraverser
  )

  private lazy val doTraverser: DoTraverser = new DoTraverserImpl(expressionTraverser, blockTraverser)

  private lazy val enumConstantListTraverser: EnumConstantListTraverser = new EnumConstantListTraverserImpl(argumentListTraverser)

  private lazy val etaTraverser: EtaTraverser = new EtaTraverserImpl(termTraverser)

  private lazy val expressionTraverser: ExpressionTraverser = new ExpressionTraverserImpl(
    ifTraverser,
    statTraverser,
    termApplyTraverser,
    termTraverser
  )

  private lazy val finallyTraverser: FinallyTraverser = new FinallyTraverserImpl(blockTraverser)

  private lazy val forTraverser: ForTraverser = new ForTraverserImpl(termApplyTraverser, ForToTermApplyTransformer)

  private lazy val forYieldTraverser: ForYieldTraverser = new ForYieldTraverserImpl(termApplyTraverser, ForYieldToTermApplyTransformer)

  private lazy val ifTraverser: IfTraverser = new IfTraverserImpl(expressionTraverser, blockTraverser)

  private lazy val importeeTraverser: ImporteeTraverser = new ImporteeTraverserImpl(nameTraverser)

  private lazy val importerTraverser: ImporterTraverser = new ImporterTraverserImpl(termRefTraverser, importeeTraverser)

  private lazy val importTraverser: ImportTraverser = new ImportTraverserImpl(
    importerTraverser,
    new CompositeImporterExcludedPredicate(CoreImporterExcludedPredicate),
    new CompositeImporterTransformer
  )

  private lazy val initArgTraverserFactory: InitArgTraverserFactory = new InitArgTraverserFactoryImpl(initTraverser)

  private lazy val initListTraverser: InitListTraverser = new InitListTraverserImpl(
    argumentListTraverser,
    initArgTraverserFactory
  )

  private lazy val initTraverser: InitTraverser = new InitTraverserImpl(
    typeTraverser,
    argumentListTraverser,
    compositeInvocationArgTraverser
  )

  private lazy val litTraverser: LitTraverser = new LitTraverserImpl()

  private lazy val modListTraverser: ModListTraverser = new ModListTraverserImpl(annotListTraverser, JavaModifiersResolver)

  private lazy val nameIndeterminateTraverser: NameIndeterminateTraverser = new NameIndeterminateTraverserImpl()

  private lazy val nameTraverser: NameTraverser = new NameTraverserImpl(
    NameAnonymousTraverser,
    nameIndeterminateTraverser,
    termNameTraverser,
    typeNameTraverser
  )

  private lazy val newAnonymousTraverser: NewAnonymousTraverser = new NewAnonymousTraverserImpl(templateTraverser)

  private lazy val newTraverser: NewTraverser = new NewTraverserImpl(
    initTraverser,
    arrayInitializerTraverser,
    ArrayInitializerContextResolver
  )

  private lazy val objectTraverser: ObjectTraverser = new ObjectTraverserImpl(
    modListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver)

  private lazy val partialFunctionTraverser: PartialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  private lazy val patExtractInfixTraverser: PatExtractInfixTraverser = new PatExtractInfixTraverserImpl(patExtractTraverser)

  private lazy val patExtractTraverser: PatExtractTraverser = new PatExtractTraverserImpl()

  private lazy val patInterpolateTraverser: PatInterpolateTraverser = new PatInterpolateTraverserImpl()

  private lazy val patListTraverser: PatListTraverser = new PatListTraverserImpl(
    argumentListTraverser,
    new SimpleArgumentTraverser(patTraverser)
  )

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

  private lazy val pkgTraverser: PkgTraverser = new PkgTraverserImpl(
    termRefTraverser,
    pkgStatListTraverser,
    new CompositeAdditionalImportersProvider(CoreAdditionalImportersProvider)
  )

  private lazy val regularClassTraverser: RegularClassTraverser = new RegularClassTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    templateTraverser,
    ParamToDeclValTransformer,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val returnTraverser: ReturnTraverser = new ReturnTraverserImpl(expressionTraverser)

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
    enumConstantListTraverser,
    statTraverser,
    DefnValClassifier,
    JavaStatClassifier
  )

  private lazy val templateTraverser: TemplateTraverser = new TemplateTraverserImpl(
    initListTraverser,
    selfTraverser,
    templateBodyTraverser,
    permittedSubTypeNameListTraverser,
    JavaInheritanceKeywordResolver,
    new CompositeTemplateInitExcludedPredicate(CoreTemplateInitExcludedPredicate)
  )

  private lazy val termAnnotateTraverser: TermAnnotateTraverser = new TermAnnotateTraverserImpl(annotListTraverser, termTraverser)

  private lazy val termApplyInfixTraverser: TermApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    expressionTraverser,
    termApplyTraverser,
    termNameTraverser,
    argumentListTraverser,
    compositeInvocationArgTraverser,
    new CompositeTermApplyInfixToTermApplyTransformer(CoreTermApplyInfixToTermApplyTransformer)
  )

  private lazy val termApplyTraverser: TermApplyTraverser = new TermApplyTraverserImpl(
    termTraverser,
    arrayInitializerTraverser,
    argumentListTraverser,
    compositeInvocationArgTraverser,
    ArrayInitializerContextResolver,
    new CompositeTermApplyTransformer(CoreTermApplyTransformer)
  )

  private lazy val termFunctionTraverser: TermFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    termParamListTraverser,
    statTraverser,
    blockTraverser
  )

  private lazy val termInterpolateTraverser: TermInterpolateTraverser = new TermInterpolateTraverserImpl(TermInterpolateTransformer, termApplyTraverser)

  private lazy val termMatchTraverser: TermMatchTraverser = new TermMatchTraverserImpl(expressionTraverser, caseTraverser)

  private lazy val termNameTraverser: TermNameTraverser = new TermNameTraverserImpl(termTraverser, TermNameTransformer)

  private lazy val termParamArgTraverserFactory: TermParamArgTraverserFactory = new TermParamArgTraverserFactoryImpl(termParamTraverser)

  private lazy val termParamListTraverser: TermParamListTraverser = new TermParamListTraverserImpl(
    argumentListTraverser,
    termParamArgTraverserFactory
  )

  private lazy val termParamTraverser: TermParamTraverser = new TermParamTraverserImpl(
    modListTraverser,
    typeTraverser,
    nameTraverser
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
    termTypeInferrer,
    new CompositeTermSelectTransformer(coreTermSelectTransformer)
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

  private lazy val throwTraverser: ThrowTraverser = new ThrowTraverserImpl(expressionTraverser)

  private lazy val traitTraverser: TraitTraverser = new TraitTraverserImpl(
    modListTraverser,
    typeParamListTraverser,
    templateTraverser,
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

  private lazy val typeAnonymousParamTraverser: TypeAnonymousParamTraverser = new TypeAnonymousParamTraverserImpl

  private lazy val typeApplyInfixTraverser: TypeApplyInfixTraverser = new TypeApplyInfixTraverserImpl

  private lazy val typeApplyTraverser: TypeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser, typeListTraverser)

  private lazy val typeBoundsTraverser: TypeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  private lazy val typeByNameTraverser: TypeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, TypeByNameToSupplierTypeTransformer)

  private lazy val typeExistentialTraverser: TypeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  private lazy val typeFunctionTraverser: TypeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser, FunctionTypeTransformer)

  private lazy val typeLambdaTraverser: TypeLambdaTraverser = new TypeLambdaTraverserImpl

  private lazy val typeListTraverser: TypeListTraverser = new TypeListTraverserImpl(
    argumentListTraverser,
    new SimpleArgumentTraverser(typeTraverser)
  )

  private lazy val typeNameTraverser: TypeNameTraverser = new TypeNameTraverserImpl(new CompositeTypeNameTransformer(CoreTypeNameTransformer))

  private lazy val typeParamListTraverser: TypeParamListTraverser = new TypeParamListTraverserImpl(
    argumentListTraverser,
    new SimpleArgumentTraverser(typeParamTraverser)
  )

  private lazy val typeParamTraverser: TypeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    typeParamListTraverser,
    typeBoundsTraverser
  )

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
    typeAnonymousParamTraverser,
    typeWildcardTraverser,
    typeByNameTraverser,
    typeRepeatedTraverser,
    typeVarTraverser
  )

  private lazy val typeTupleTraverser: TypeTupleTraverser = new TypeTupleTraverserImpl(
    typeApplyTraverser,
    TypeTupleToTypeApplyTransformer
  )

  private lazy val typeVarTraverser: TypeVarTraverser = new TypeVarTraverserImpl

  private lazy val typeWildcardTraverser: TypeWildcardTraverser = new TypeWildcardTraverserImpl(typeBoundsTraverser)

  private lazy val typeWithTraverser: TypeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  private lazy val whileTraverser: WhileTraverser = new WhileTraverserImpl(expressionTraverser, blockTraverser)
}
