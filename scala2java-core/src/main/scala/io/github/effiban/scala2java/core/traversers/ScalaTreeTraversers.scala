package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{Classifiers, DefnValClassifier, JavaStatClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.{Factories, TemplateChildContextFactory}
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.predicates._
import io.github.effiban.scala2java.core.providers.{CompositeAdditionalImportersProvider, CoreAdditionalImportersProvider}
import io.github.effiban.scala2java.core.renderers.Renderers
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.resolvers._
import io.github.effiban.scala2java.core.transformers._
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Assign

class ScalaTreeTraversers(implicit factories: Factories,
                          typeInferrers: TypeInferrers,
                          javaWriter: JavaWriter,
                          extensionRegistry: ExtensionRegistry) {

  private implicit lazy val classifiers: Classifiers = new Classifiers(typeInferrers)
  private implicit lazy val transformers: Transformers = new Transformers(typeInferrers)
  private lazy val resolvers = new Resolvers()
  private lazy val renderers = new Renderers()

  import factories._
  import io.github.effiban.scala2java.core.renderers.contextfactories.RenderContextFactories._
  import renderers._
  import resolvers._
  import transformers._
  import typeInferrers._


  private lazy val alternativeTraverser: AlternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  private lazy val deprecatedAnnotListTraverser: DeprecatedAnnotListTraverser = new DeprecatedAnnotListTraverserImpl(deprecatedAnnotTraverser)

  private lazy val deprecatedAnnotTraverser: DeprecatedAnnotTraverser = new DeprecatedAnnotTraverserImpl(deprecatedInitTraverser)

  private lazy val annotTraverser: AnnotTraverser = new AnnotTraverserImpl(initTraverser)

  private lazy val deprecatedAnonymousFunctionTraverser: DeprecatedAnonymousFunctionTraverser = new DeprecatedAnonymousFunctionTraverserImpl(deprecatedTermFunctionTraverser)

  private lazy val anonymousFunctionTraverser: AnonymousFunctionTraverser = new AnonymousFunctionTraverserImpl(termFunctionTraverser)

  private lazy val applyTypeTraverser: ApplyTypeTraverser = new ApplyTypeTraverserImpl(expressionTermTraverser, typeTraverser)

  private lazy val deprecatedApplyUnaryTraverser: DeprecatedApplyUnaryTraverser = new DeprecatedApplyUnaryTraverserImpl(termNameRenderer, deprecatedExpressionTermTraverser)

  private lazy val applyUnaryTraverser: ApplyUnaryTraverser = new ApplyUnaryTraverserImpl(expressionTermTraverser)

  private lazy val deprecatedArgumentListTraverser: DeprecatedArgumentListTraverser = new DeprecatedArgumentListTraverserImpl

  private lazy val deprecatedArrayInitializerTraverser: DeprecatedArrayInitializerTraverser = new DeprecatedArrayInitializerTraverserImpl(
    typeTraverser,
    typeRenderer,
    deprecatedExpressionTermTraverser,
    new DeprecatedSimpleArgumentTraverser(deprecatedExpressionTermTraverser),
    deprecatedArgumentListTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  private lazy val arrayInitializerTraverser: ArrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    expressionTermTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  private lazy val deprecatedAscribeTraverser: DeprecatedAscribeTraverser = new DeprecatedAscribeTraverserImpl(
    typeTraverser,
    typeRenderer,
    deprecatedExpressionTermTraverser
  )

  private lazy val ascribeTraverser: AscribeTraverser = new AscribeTraverserImpl(
    expressionTermTraverser,
    typeTraverser
  )

  private lazy val deprecatedAssignInvocationArgTraverser: DeprecatedInvocationArgTraverser[Assign] = new DeprecatedAssignInvocationArgTraverser(
    deprcatedAssignLHSTraverser,
    deprecatedExpressionTermTraverser
  )

  private lazy val deprcatedAssignLHSTraverser: DeprecatedAssignLHSTraverser = new DeprecatedAssignLHSTraverserImpl(deprecatedExpressionTermTraverser)

  private lazy val deprecatedAssignTraverser: DeprecatedAssignTraverser = new DeprecatedAssignTraverserImpl(deprcatedAssignLHSTraverser, deprecatedExpressionTermTraverser)

  private lazy val assignTraverser: AssignTraverser = new AssignTraverserImpl(expressionTermTraverser)

  private lazy val deprecatedBlockStatTraverser: DeprecatedBlockStatTraverser = new DeprecatedBlockStatTraverserImpl(
    deprecatedIfTraverser,
    deprecatedTryTraverser,
    deprecatedTryWithHandlerTraverser,
    statTraverser,
    shouldReturnValueResolver,
    JavaStatClassifier
  )

  private lazy val blockLastStatTraverser: BlockLastStatTraverser = new BlockLastStatTraverserImpl(
    blockStatTraverser,
    defaultIfTraverser,
    tryTraverser,
    tryWithHandlerTraverser,
    shouldReturnValueResolver
  )

  private lazy val blockStatTraverser: BlockStatTraverser = new BlockStatTraverserImpl(
    statTermTraverser,
    defnValTraverser,
    defnVarTraverser,
    declVarTraverser
  )

  private lazy val deprecatedBlockTraverser: DeprecatedBlockTraverser = new DeprecatedBlockTraverserImpl(
    deprecatedInitTraverser,
    deprecatedBlockStatTraverser
  )

  private lazy val blockWrappingTermTraverser: BlockWrappingTermTraverser = new BlockWrappingTermTraverserImpl(defaultBlockTraverser)

  private lazy val caseClassTraverser: CaseClassTraverser = new CaseClassTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    termParamTraverser,
    termParamListRenderer,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val deprecatedCaseTraverser: DeprecatedCaseTraverser = new DeprecatedCaseTraverserImpl(
    patTraverser,
    patRenderer,
    deprecatedExpressionTermTraverser
  )

  private lazy val caseTraverser: CaseTraverser = new CaseTraverserImpl(patTraverser, expressionTermTraverser)

  private lazy val deprecatedCatchHandlerTraverser: DeprecatedCatchHandlerTraverser = new DeprecatedCatchHandlerTraverserImpl(
    CatchArgumentTraverser,
    catchArgumentRenderer,
    deprecatedBlockTraverser
  )

  private lazy val catchHandlerTraverser: CatchHandlerTraverser = new CatchHandlerTraverserImpl(
    CatchArgumentTraverser,
    blockWrappingTermTraverser
  )

  private lazy val classTraverser: ClassTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    new CompositeClassTransformer(),
  )

  @deprecated
  private lazy val deprecatedClassOfTraverser: DeprecatedClassOfTraverser = new DeprecatedClassOfTraverserImpl(typeTraverser)

  private lazy val deprecatedCompositeInvocationArgTraverser: DeprecatedInvocationArgTraverser[Term] = new DeprecatedCompositeInvocationArgTraverser(
    deprecatedAssignInvocationArgTraverser,
    deprecatedExpressionTermTraverser
  )

  private lazy val ctorPrimaryTraverser: CtorPrimaryTraverser = new CtorPrimaryTraverserImpl(CtorPrimaryTransformer, defnDefTraverser)

  private lazy val ctorSecondaryTraverser: CtorSecondaryTraverser = new CtorSecondaryTraverserImpl(CtorSecondaryTransformer, defnDefTraverser)

  private lazy val declDefTraverser: DeclDefTraverser = new DeclDefTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    termNameRenderer,
    termParamTraverser,
    termParamListRenderer
  )

  private lazy val declTraverser: DeclTraverser = new DeclTraverserImpl(
    declValTraverser,
    deprecatedDeclVarTraverser,
    declDefTraverser,
    declTypeTraverser)

  private lazy val declTypeTraverser: DeclTypeTraverser = new DeclTypeTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    JavaTreeTypeResolver)

  private lazy val declValTraverser: DeclValTraverser = new DeclValTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )

  private lazy val deprecatedDeclVarTraverser: DeprecatedDeclVarTraverser = new DeprecatedDeclVarTraverserImpl(
    deprecatedModListTraverser,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )

  private lazy val declVarTraverser: DeclVarTraverser = new DeclVarTraverserImpl(
    modListTraverser,
    typeTraverser,
    patTraverser
  )
  
  private lazy val defaultBlockTraverser: DefaultBlockTraverser = new DefaultBlockTraverserImpl(
    initTraverser,
    blockStatTraverser,
    blockLastStatTraverser
  )

  private lazy val defaultIfTraverser: DefaultIfTraverser = new DefaultIfTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)

  private lazy val defaultTermRefTraverser: DefaultTermRefTraverser = new DefaultTermRefTraverserImpl(
    thisTraverser,
    superTraverser,
    defaultTermSelectTraverser
  )

  private lazy val defaultTermSelectTraverser: DefaultTermSelectTraverser = new DefaultTermSelectTraverserImpl(defaultTermRefTraverser)

  private lazy val deprecatedDefaultTermTraverser: DeprecatedDefaultTermTraverser = new DeprecatedDefaultTermTraverserImpl(
    defaultTermRefTraverser,
    deprecatedTermApplyTraverser,
    deprecatedMainApplyTypeTraverser,
    deprecatedTermApplyInfixTraverser,
    deprecatedAssignTraverser,
    deprecatedReturnTraverser,
    deprecatedThrowTraverser,
    deprecatedAscribeTraverser,
    deprecatedTermAnnotateTraverser,
    deprecatedTermTupleTraverser,
    deprecatedBlockTraverser,
    deprecatedIfTraverser,
    deprecatedTermMatchTraverser,
    deprecatedTryTraverser,
    deprecatedTryWithHandlerTraverser,
    deprecatedTermFunctionTraverser,
    deprecatedPartialFunctionTraverser,
    deprecatedAnonymousFunctionTraverser,
    deprecatedWhileTraverser,
    deprecatedDoTraverser,
    deprecatedNewTraverser,
    deprecatedNewAnonymousTraverser,
    termPlaceholderRenderer,
    deprecatedEtaTraverser,
    deprecatedTermRepeatedTraverser,
    defaultTermRenderer
  )

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
    etaTraverser,
    termRepeatedTraverser
  )

  private lazy val defnDefTraverser: DefnDefTraverser = new DefnDefTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    termNameRenderer,
    typeTraverser,
    typeRenderer,
    termParamTraverser,
    termParamListRenderer,
    blockWrappingTermTraverser,
    blockRenderContextFactory,
    blockRenderer,
    termTypeInferrer,
    new CompositeDefnDefTransformer()
  )

  private lazy val defnTraverser: DefnTraverser = new DefnTraverserImpl(
    deprecatedDefnValTraverser,
    deprecatedDefnVarTraverser,
    defnDefTraverser,
    defnTypeTraverser,
    classTraverser,
    traitTraverser,
    objectTraverser
  )

  private lazy val defnTypeTraverser: DefnTypeTraverser = new DefnTypeTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    typeBoundsTraverser,
    typeBoundsRenderer,
    JavaTreeTypeResolver
  )

  private lazy val deprecatedDefnValOrVarTypeTraverser: DeprecatedDefnValOrVarTypeTraverser = new DeprecatedDefnValOrVarTypeTraverserImpl(
    typeTraverser,
    typeRenderer,
    termTypeInferrer
  )

  private lazy val defnValOrVarTypeTraverser: DefnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(
    typeTraverser,
    termTypeInferrer
  )

  private lazy val deprecatedDefnValTraverser: DeprecatedDefnValTraverser = new DeprecatedDefnValTraverserImpl(
    deprecatedModListTraverser,
    deprecatedDefnValOrVarTypeTraverser,
    patTraverser,
    patListRenderer,
    deprecatedExpressionTermTraverser,
    deprecatedDeclVarTraverser,
    new CompositeDefnValToDeclVarTransformer,
    new CompositeDefnValTransformer
  )

  private lazy val defnValTraverser: DefnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    expressionTermTraverser,
    declVarTraverser,
    new CompositeDefnValToDeclVarTransformer,
    new CompositeDefnValTransformer
  )

  private lazy val deprecatedDefnVarTraverser: DeprecatedDefnVarTraverser = new DeprecatedDefnVarTraverserImpl(
    deprecatedModListTraverser,
    deprecatedDefnValOrVarTypeTraverser,
    patTraverser,
    patListRenderer,
    deprecatedExpressionTermTraverser
  )

  private lazy val defnVarTraverser: DefnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    expressionTermTraverser
  )

  private lazy val deprecatedDoTraverser: DeprecatedDoTraverser = new DeprecatedDoTraverserImpl(deprecatedExpressionTermTraverser, deprecatedBlockTraverser)

  private lazy val doTraverser: DoTraverser = new DoTraverserImpl(blockWrappingTermTraverser, expressionTermTraverser)

  private lazy val enumConstantListTraverser: EnumConstantListTraverser = new EnumConstantListTraverserImpl(deprecatedArgumentListTraverser)

  private lazy val deprecatedEtaTraverser: DeprecatedEtaTraverser = new DeprecatedEtaTraverserImpl(deprecatedExpressionTermTraverser)

  private lazy val etaTraverser: EtaTraverser = new EtaTraverserImpl(expressionTermTraverser)

  private lazy val expressionBlockTraverser: ExpressionBlockTraverser = new ExpressionBlockTraverserImpl(expressionTermTraverser)

  private lazy val expressionIfTraverser: ExpressionIfTraverser = new ExpressionIfTraverserImpl(expressionTermTraverser)

  private lazy val expressionTermNameTraverser: ExpressionTermNameTraverser = new ExpressionTermNameTraverserImpl(
    expressionTermTraverser,
    new CompositeTermNameTransformer(CoreTermNameTransformer)
  )

  private lazy val deprecatedExpressionTermRefTraverser: DeprecatedExpressionTermRefTraverser = new DeprecatedExpressionTermRefTraverserImpl(
    deprecatedTermNameTraverser,
    deprecatedExpressionTermSelectTraverser,
    deprecatedApplyUnaryTraverser,
    defaultTermRefTraverser,
    defaultTermRefRenderer
  )

  private lazy val expressionTermRefTraverser: ExpressionTermRefTraverser = new ExpressionTermRefTraverserImpl(
    expressionTermNameTraverser,
    expressionTermSelectTraverser,
    applyUnaryTraverser,
    defaultTermRefTraverser
  )

  private lazy val deprecatedExpressionTermSelectTraverser: DeprecatedExpressionTermSelectTraverser = new DeprecatedExpressionTermSelectTraverserImpl(
    deprecatedExpressionTermTraverser,
    termNameRenderer,
    typeTraverser,
    typeListRenderer,
    qualifierTypeInferrer,
    new CompositeTermSelectTransformer(CoreTermSelectTransformer)
  )

  private lazy val expressionTermSelectTraverser: ExpressionTermSelectTraverser = new ExpressionTermSelectTraverserImpl(
    expressionTermTraverser,
    qualifierTypeInferrer,
    new CompositeTermSelectTransformer(CoreTermSelectTransformer)
  )

  private lazy val deprecatedExpressionTermTraverser: DeprecatedExpressionTermTraverser = new DeprecatedExpressionTermTraverserImpl(
    deprecatedIfTraverser,
    statTraverser,
    deprecatedTermApplyTraverser,
    deprecatedExpressionTermRefTraverser,
    deprecatedDefaultTermTraverser
  )

  private lazy val expressionTermTraverser: ExpressionTermTraverser = new ExpressionTermTraverserImpl(
    expressionTermRefTraverser,
    expressionBlockTraverser,
    expressionIfTraverser,
    defaultTermTraverser
  )

  private lazy val deprecatedFinallyTraverser: DeprecatedFinallyTraverser = new DeprecatedFinallyTraverserImpl(deprecatedBlockTraverser)

  private lazy val finallyTraverser: FinallyTraverser = new FinallyTraverserImpl(blockWrappingTermTraverser)

  private lazy val deprecatedIfTraverser: DeprecatedIfTraverser = new DeprecatedIfTraverserImpl(deprecatedExpressionTermTraverser, deprecatedBlockTraverser)

  private lazy val importerTraverser: ImporterTraverser = new ImporterTraverserImpl(defaultTermRefTraverser)

  private lazy val importTraverser: ImportTraverser = new ImportTraverserImpl(
    importerTraverser,
    new CompositeImporterExcludedPredicate(CoreImporterExcludedPredicate),
    new CompositeImporterTransformer
  )

  private lazy val deprecatedInitArgTraverserFactory: DeprecatedInitArgTraverserFactory = new DeprecatedInitArgTraverserFactoryImpl(deprecatedInitTraverser)

  private lazy val deprecatedInitListTraverser: DeprecatedInitListTraverser = new DeprecatedInitListTraverserImpl(
    deprecatedArgumentListTraverser,
    deprecatedInitArgTraverserFactory
  )

  private lazy val deprecatedInitTraverser: DeprecatedInitTraverser = new DeprecatedInitTraverserImpl(
    typeTraverser,
    typeRenderer,
    deprecatedArgumentListTraverser,
    deprecatedCompositeInvocationArgTraverser
  )

  private lazy val initTraverser: InitTraverser = new InitTraverserImpl(typeTraverser, expressionTermTraverser)

  private lazy val deprecatedMainApplyTypeTraverser: DeprecatedMainApplyTypeTraverser =
    new DeprecatedMainApplyTypeTraverserImpl(
      deprecatedClassOfTraverser,
      classOfRenderer,
      deprecatedStandardApplyTypeTraverser
    )

  private lazy val deprecatedModListTraverser: DeprecatedModListTraverser = new DeprecatedModListTraverserImpl(deprecatedAnnotListTraverser, JavaModifiersResolver)

  private lazy val modListTraverser: ModListTraverser = new ModListTraverserImpl(annotTraverser, JavaModifiersResolver)

  private lazy val nameTraverser: NameTraverser = new NameTraverserImpl(typeNameTraverser)

  private lazy val deprecatedNewAnonymousTraverser: DeprecatedNewAnonymousTraverser = new DeprecatedNewAnonymousTraverserImpl(templateTraverser)

  private lazy val deprecatedNewTraverser: DeprecatedNewTraverser = new DeprecatedNewTraverserImpl(
    deprecatedInitTraverser,
    deprecatedArrayInitializerTraverser,
    ArrayInitializerContextResolver
  )

  private lazy val newTraverser: NewTraverser = new NewTraverserImpl(
    initTraverser,
    arrayInitializerTraverser,
    ArrayInitializerContextResolver
  )

  private lazy val objectTraverser: ObjectTraverser = new ObjectTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver)

  private lazy val deprecatedPartialFunctionTraverser: DeprecatedPartialFunctionTraverser = new DeprecatedPartialFunctionTraverserImpl(deprecatedTermFunctionTraverser)

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

  private lazy val permittedSubTypeNameListTraverser = new PermittedSubTypeNameListTraverserImpl(deprecatedArgumentListTraverser)

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
    defaultTermRefTraverser,
    defaultTermRefRenderer,
    pkgStatListTraverser,
    new CompositeAdditionalImportersProvider(CoreAdditionalImportersProvider)
  )

  private lazy val regularClassTraverser: RegularClassTraverser = new RegularClassTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    templateTraverser,
    ParamToDeclValTransformer,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val deprecatedReturnTraverser: DeprecatedReturnTraverser = new DeprecatedReturnTraverserImpl(deprecatedExpressionTermTraverser)

  private lazy val returnTraverser: ReturnTraverser = new ReturnTraverserImpl(expressionTermTraverser)

  private lazy val selfTraverser: SelfTraverser = new SelfTraverserImpl(selfRenderer)

  lazy val sourceTraverser: SourceTraverser = new SourceTraverserImpl(statTraverser)

  private lazy val statTermTraverser: StatTermTraverser = new StatTermTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser
  )

  private lazy val statTraverser: StatTraverser = new StatTraverserImpl(
    statTermTraverser,
    statTermRenderer,
    importTraverser,
    importRenderer,
    pkgTraverser,
    defnTraverser,
    declTraverser
  )

  private lazy val deprecatedStandardApplyTypeTraverser: DeprecatedStandardApplyTypeTraverser = new DeprecatedStandardApplyTypeTraverserImpl(
    deprecatedExpressionTermSelectTraverser,
    typeTraverser,
    typeListRenderer,
    deprecatedExpressionTermTraverser
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
    deprecatedInitListTraverser,
    selfTraverser,
    templateBodyTraverser,
    permittedSubTypeNameListTraverser,
    JavaInheritanceKeywordResolver,
    new CompositeTemplateInitExcludedPredicate(CoreTemplateInitExcludedPredicate)
  )

  private lazy val deprecatedTermAnnotateTraverser: DeprecatedTermAnnotateTraverser = new DeprecatedTermAnnotateTraverserImpl(deprecatedAnnotListTraverser, deprecatedExpressionTermTraverser)

  private lazy val termAnnotateTraverser: TermAnnotateTraverser = new TermAnnotateTraverserImpl(expressionTermTraverser, annotTraverser)

  private lazy val deprecatedTermApplyInfixTraverser: DeprecatedTermApplyInfixTraverser = new DeprecatedTermApplyInfixTraverserImpl(
    deprecatedExpressionTermTraverser,
    deprecatedTermApplyTraverser,
    termNameRenderer,
    new CompositeTermApplyInfixToTermApplyTransformer(CoreTermApplyInfixToTermApplyTransformer)
  )

  private lazy val termApplyInfixTraverser: TermApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    expressionTermTraverser,
    termApplyTraverser,
    new CompositeTermApplyInfixToTermApplyTransformer(CoreTermApplyInfixToTermApplyTransformer)
  )

  private lazy val deprecatedTermApplyTraverser: DeprecatedTermApplyTraverser = new DeprecatedTermApplyTraverserImpl(
    deprecatedExpressionTermTraverser,
    deprecatedArrayInitializerTraverser,
    deprecatedArgumentListTraverser,
    deprecatedCompositeInvocationArgTraverser,
    termApplyTransformationContextFactory,
    ArrayInitializerContextResolver,
    internalTermApplyTransformer
  )

  private lazy val termApplyTraverser: TermApplyTraverser = new TermApplyTraverserImpl(
    expressionTermTraverser,
    arrayInitializerTraverser,
    termApplyTransformationContextFactory,
    ArrayInitializerContextResolver,
    internalTermApplyTransformer
  )

  private lazy val deprecatedTermFunctionTraverser: DeprecatedTermFunctionTraverser = new DeprecatedTermFunctionTraverserImpl(
    deprecatedTermParamTraverser,
    deprecatedTermParamListTraverser,
    statTraverser,
    deprecatedBlockTraverser
  )

  private lazy val termFunctionTraverser: TermFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    defaultBlockTraverser,
    defaultTermTraverser
  )

  private lazy val deprecatedTermMatchTraverser: DeprecatedTermMatchTraverser = new DeprecatedTermMatchTraverserImpl(deprecatedExpressionTermTraverser, deprecatedCaseTraverser)

  private lazy val termMatchTraverser: TermMatchTraverser = new TermMatchTraverserImpl(expressionTermTraverser, caseTraverser)

  private lazy val deprecatedTermNameTraverser: DeprecatedTermNameTraverser = new DeprecatedTermNameTraverserImpl(
    deprecatedTermNameWithoutRenderTraverser,
    termNameRenderer
  )

  private lazy val deprecatedTermNameWithoutRenderTraverser: DeprecatedTermNameWithoutRenderTraverser = new DeprecatedTermNameWithoutRenderTraverserImpl(
    deprecatedExpressionTermTraverser,
    new CompositeTermNameTransformer(CoreTermNameTransformer),
  )

  private lazy val deprecatedTermParamArgTraverserFactory: DeprecatedTermParamArgTraverserFactory = new DeprecatedTermParamArgTraverserFactoryImpl(deprecatedTermParamTraverser)

  private lazy val deprecatedTermParamListTraverser: DeprecatedTermParamListTraverser = new DeprecatedTermParamListTraverserImpl(
    deprecatedArgumentListTraverser,
    deprecatedTermParamArgTraverserFactory
  )

  private lazy val deprecatedTermParamTraverser: DeprecatedTermParamTraverser = new DeprecatedTermParamTraverserImpl(
    deprecatedModListTraverser,
    typeTraverser,
    typeRenderer,
    nameTraverser,
    nameRenderer
  )

  private lazy val termParamTraverser: TermParamTraverser = new TermParamTraverserImpl(
    modListTraverser,
    nameTraverser,
    typeTraverser,
    expressionTermTraverser
  )

  private lazy val deprecatedTermRepeatedTraverser: DeprecatedTermRepeatedTraverser = new DeprecatedTermRepeatedTraverserImpl(deprecatedExpressionTermTraverser)

  private lazy val termRepeatedTraverser: TermRepeatedTraverser = new TermRepeatedTraverserImpl(expressionTermTraverser)

  private lazy val deprecatedTermTupleTraverser: DeprecatedTermTupleTraverser = new DeprecatedTermTupleTraverserImpl(
    deprecatedTermApplyTraverser,
    TermTupleToTermApplyTransformer
  )

  private lazy val termTupleTraverser: TermTupleTraverser = new TermTupleTraverserImpl(termApplyTraverser, TermTupleToTermApplyTransformer)

  private lazy val thisTraverser: ThisTraverser = new ThisTraverserImpl(nameTraverser)

  private lazy val deprecatedThrowTraverser: DeprecatedThrowTraverser = new DeprecatedThrowTraverserImpl(deprecatedExpressionTermTraverser)

  private lazy val throwTraverser: ThrowTraverser = new ThrowTraverserImpl(expressionTermTraverser)

  private lazy val traitTraverser: TraitTraverser = new TraitTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val deprecatedTryTraverser: DeprecatedTryTraverser = new DeprecatedTryTraverserImpl(
    deprecatedBlockTraverser,
    deprecatedCatchHandlerTraverser,
    deprecatedFinallyTraverser
  )

  private lazy val tryTraverser: TryTraverser = new TryTraverserImpl(
    blockWrappingTermTraverser,
    catchHandlerTraverser,
    finallyTraverser
  )

  private lazy val deprecatedTryWithHandlerTraverser: DeprecatedTryWithHandlerTraverser = new DeprecatedTryWithHandlerTraverserImpl(deprecatedBlockTraverser, deprecatedFinallyTraverser)

  private lazy val tryWithHandlerTraverser: TryWithHandlerTraverser = new TryWithHandlerTraverserImpl(blockWrappingTermTraverser, finallyTraverser)

  private lazy val typeAnnotateTraverser: TypeAnnotateTraverser = new TypeAnnotateTraverserImpl(typeTraverser)

  private lazy val typeApplyInfixTraverser: TypeApplyInfixTraverser = new TypeApplyInfixTraverserImpl()

  private lazy val typeApplyTraverser: TypeApplyTraverser = new TypeApplyTraverserImpl(typeTraverser)

  private lazy val typeBoundsTraverser: TypeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  private lazy val typeByNameTraverser: TypeByNameTraverser = new TypeByNameTraverserImpl(typeApplyTraverser, TypeByNameToSupplierTypeTransformer)

  private lazy val typeExistentialTraverser: TypeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  private lazy val typeFunctionTraverser: TypeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser, FunctionTypeTransformer)

  private lazy val typeNameTraverser: TypeNameTraverser = new TypeNameTraverserImpl(new CompositeTypeNameTransformer(CoreTypeNameTransformer))

  private lazy val typeParamListTraverser: TypeParamListTraverser = new TypeParamListTraverserImpl(
    deprecatedArgumentListTraverser,
    new DeprecatedSimpleArgumentTraverser(typeParamTraverser)
  )

  private lazy val typeParamTraverser: TypeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    nameRenderer,
    typeParamListTraverser,
    typeBoundsTraverser,
    typeBoundsRenderer
  )

  private lazy val typeProjectTraverser: TypeProjectTraverser = new TypeProjectTraverserImpl(
    typeTraverser,
    typeNameTraverser
  )

  private lazy val typeRefineTraverser: TypeRefineTraverser = new TypeRefineTraverserImpl(typeTraverser)

  private lazy val typeRefTraverser: TypeRefTraverser = new TypeRefTraverserImpl(
    typeNameTraverser,
    typeSelectTraverser,
    typeSingletonTraverser,
    typeProjectTraverser
  )

  private lazy val typeRepeatedTraverser: TypeRepeatedTraverser = new TypeRepeatedTraverserImpl(typeTraverser)

  private lazy val typeSelectTraverser: TypeSelectTraverser = new TypeSelectTraverserImpl(
    defaultTermRefTraverser,
    typeNameTraverser
  )

  private lazy val typeSingletonTraverser: TypeSingletonTraverser = new TypeSingletonTraverserImpl(thisTraverser)

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

  private lazy val typeTupleTraverser: TypeTupleTraverser = new TypeTupleTraverserImpl(
    typeApplyTraverser,
    TypeTupleToTypeApplyTransformer
  )

  private lazy val typeWildcardTraverser: TypeWildcardTraverser = new TypeWildcardTraverserImpl(typeBoundsTraverser)

  private lazy val typeWithTraverser: TypeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  private lazy val deprecatedWhileTraverser: DeprecatedWhileTraverser = new DeprecatedWhileTraverserImpl(deprecatedExpressionTermTraverser, deprecatedBlockTraverser)

  private lazy val whileTraverser: WhileTraverser = new WhileTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)
}
