package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.{Classifiers, DefnVarClassifier, JavaStatClassifier}
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

class ScalaTreeTraversers(implicit factories: Factories,
                          typeInferrers: TypeInferrers,
                          javaWriter: JavaWriter,
                          extensionRegistry: ExtensionRegistry) {

  private implicit lazy val classifiers: Classifiers = new Classifiers(typeInferrers)
  private implicit lazy val transformers: Transformers = new Transformers(typeInferrers)
  private lazy val resolvers = new Resolvers()
  private lazy val renderers = new Renderers()

  import factories._
  import renderers._
  import resolvers._
  import transformers._
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
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    termParamTraverser,
    termParamListRenderer,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private lazy val caseTraverser: CaseTraverser = new CaseTraverserImpl(patTraverser, expressionTermTraverser)

  private lazy val catchHandlerTraverser: CatchHandlerTraverser = new CatchHandlerTraverserImpl(
    CatchArgumentTraverser,
    blockWrappingTermTraverser
  )

  private lazy val classTraverser: ClassTraverser = new ClassTraverserImpl(
    caseClassTraverser,
    regularClassTraverser,
    new CompositeClassTransformer(),
  )

  private lazy val ctorPrimaryTraverser: CtorPrimaryTraverser = new CtorPrimaryTraverserImpl(CtorPrimaryTransformer, defnDefTraverser)

  private lazy val ctorSecondaryTraverser: CtorSecondaryTraverser = new CtorSecondaryTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeNameTraverser,
    typeNameRenderer,
    termParamTraverser,
    termParamListRenderer,
    initTraverser,
    initRenderer,
    blockStatTraverser,
    blockStatRenderer
  )

  private lazy val declDefTraverser: DeclDefTraverser = new DeclDefTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    typeTraverser,
    typeRenderer,
    termNameRenderer,
    termParamTraverser,
    termParamListRenderer
  )

  private lazy val declTraverser: DeclTraverser = new DeclTraverserImpl(
    declVarTraverser,
    declVarRenderer,
    declDefTraverser)

  private lazy val declVarTraverser: DeclVarTraverser = new DeclVarTraverserImpl(
    modListTraverser,
    typeTraverser,
    patTraverser
  )

  private lazy val defaultBlockTraverser: DefaultBlockTraverser = new DefaultBlockTraverserImpl(
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
    typeParamTraverser,
    typeParamListRenderer,
    termNameRenderer,
    typeTraverser,
    typeRenderer,
    termParamTraverser,
    termParamListRenderer,
    blockWrappingTermTraverser,
    blockRenderer,
    termTypeInferrer,
    new CompositeDefnDefTransformer()
  )

  private lazy val defnTraverser: DefnTraverser = new DefnTraverserImpl(
    declVarRenderer,
    defnVarTraverser,
    defnVarRenderer,
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
    typeParamTraverser,
    typeParamListRenderer,
    typeTraverser,
    typeRenderer,
    typeBoundsTraverser,
    typeBoundsRenderer,
    JavaTreeTypeResolver
  )

  private lazy val defnValOrVarTypeTraverser: DefnValOrVarTypeTraverser = new DefnValOrVarTypeTraverserImpl(
    typeTraverser,
    termTypeInferrer
  )

  private lazy val defnVarTraverser: DefnVarTraverser = new DefnVarTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patTraverser,
    expressionTermTraverser,
    declVarTraverser,
    new CompositeDefnVarToDeclVarTransformer,
    new CompositeDefnVarTransformer
  )

  private lazy val doTraverser: DoTraverser = new DoTraverserImpl(blockWrappingTermTraverser, expressionTermTraverser)

  private lazy val enumConstantListTraverser: EnumConstantListTraverser = new EnumConstantListTraverserImpl(argumentListRenderer)

  private lazy val etaTraverser: EtaTraverser = new EtaTraverserImpl(expressionTermTraverser)

  private lazy val expressionBlockTraverser: ExpressionBlockTraverser = new ExpressionBlockTraverserImpl(expressionTermTraverser)

  private lazy val expressionIfTraverser: ExpressionIfTraverser = new ExpressionIfTraverserImpl(expressionTermTraverser)

  private lazy val expressionTermNameTraverser: ExpressionTermNameTraverser = new ExpressionTermNameTraverserImpl(
    expressionTermTraverser,
    new CompositeTermNameTransformer(CoreTermNameTransformer)
  )

  private lazy val expressionTermRefTraverser: ExpressionTermRefTraverser = new ExpressionTermRefTraverserImpl(
    expressionTermNameTraverser,
    expressionTermSelectTraverser,
    applyUnaryTraverser,
    defaultTermRefTraverser
  )

  private lazy val expressionTermSelectTraverser: ExpressionTermSelectTraverser = new ExpressionTermSelectTraverserImpl(
    expressionTermTraverser,
    qualifierTypeInferrer,
    new CompositeTermSelectTransformer(CoreTermSelectTransformer)
  )

  private lazy val expressionTermTraverser: ExpressionTermTraverser = new ExpressionTermTraverserImpl(
    expressionTermRefTraverser,
    expressionBlockTraverser,
    expressionIfTraverser,
    defaultTermTraverser
  )

  private lazy val finallyTraverser: FinallyTraverser = new FinallyTraverserImpl(blockWrappingTermTraverser)

  private lazy val importerTraverser: ImporterTraverser = new ImporterTraverserImpl(defaultTermRefTraverser)

  private lazy val importTraverser: ImportTraverser = new ImportTraverserImpl(
    importerTraverser,
    new CompositeImporterExcludedPredicate(CoreImporterExcludedPredicate),
    new CompositeImporterTransformer
  )

  private lazy val initTraverser: InitTraverser = new InitTraverserImpl(typeTraverser, expressionTermTraverser)

  private lazy val modListTraverser: ModListTraverser = new ModListTraverserImpl(annotTraverser, JavaModifiersResolver)

  private lazy val nameTraverser: NameTraverser = new NameTraverserImpl(typeNameTraverser)

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

  private lazy val permittedSubTypeNameListTraverser = new PermittedSubTypeNameListTraverserImpl(argumentListRenderer)

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
    typeParamTraverser,
    typeParamListRenderer,
    templateTraverser,
    ParamToDeclVarTransformer,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

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
    DefnVarClassifier,
    JavaStatClassifier
  )

  private lazy val templateTraverser: TemplateTraverser = new TemplateTraverserImpl(
    initTraverser,
    initListRenderer,
    selfTraverser,
    templateBodyTraverser,
    permittedSubTypeNameListTraverser,
    JavaInheritanceKeywordResolver,
    new CompositeTemplateInitExcludedPredicate(CoreTemplateInitExcludedPredicate)
  )

  private lazy val termAnnotateTraverser: TermAnnotateTraverser = new TermAnnotateTraverserImpl(expressionTermTraverser, annotTraverser)

  private lazy val termApplyInfixTraverser: TermApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    expressionTermTraverser,
    termApplyTraverser,
    new CompositeTermApplyInfixToTermApplyTransformer(CoreTermApplyInfixToTermApplyTransformer)
  )

  private lazy val termApplyTraverser: TermApplyTraverser = new TermApplyTraverserImpl(
    expressionTermTraverser,
    arrayInitializerTraverser,
    termApplyTransformationContextFactory,
    ArrayInitializerContextResolver,
    internalTermApplyTransformer
  )

  private lazy val termFunctionTraverser: TermFunctionTraverser = new TermFunctionTraverserImpl(
    termParamTraverser,
    defaultBlockTraverser,
    defaultTermTraverser
  )

  private lazy val termMatchTraverser: TermMatchTraverser = new TermMatchTraverserImpl(expressionTermTraverser, caseTraverser)

  private lazy val termParamTraverser: TermParamTraverser = new TermParamTraverserImpl(
    modListTraverser,
    nameTraverser,
    typeTraverser,
    expressionTermTraverser
  )

  private lazy val termRepeatedTraverser: TermRepeatedTraverser = new TermRepeatedTraverserImpl(expressionTermTraverser)

  private lazy val termTupleTraverser: TermTupleTraverser = new TermTupleTraverserImpl(termApplyTraverser, TermTupleToTermApplyTransformer)

  private lazy val thisTraverser: ThisTraverser = new ThisTraverserImpl(nameTraverser)

  private lazy val throwTraverser: ThrowTraverser = new ThrowTraverserImpl(expressionTermTraverser)

  private lazy val traitTraverser: TraitTraverser = new TraitTraverserImpl(
    modListTraverser,
    ModifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    templateTraverser,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
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

  private lazy val typeFunctionTraverser: TypeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser, FunctionTypeTransformer)

  private lazy val typeNameTraverser: TypeNameTraverser = new TypeNameTraverserImpl(new CompositeTypeNameTransformer(CoreTypeNameTransformer))

  private lazy val typeParamTraverser: TypeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    typeBoundsTraverser
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

  private lazy val whileTraverser: WhileTraverser = new WhileTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)
}
