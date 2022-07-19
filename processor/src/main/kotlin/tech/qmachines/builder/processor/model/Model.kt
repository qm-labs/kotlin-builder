package tech.qmachines.builder.processor.model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*

internal class Model(
    val withBuilderClass: KSClassDeclaration
) {
    val packageName: String = withBuilderClass.packageName.asString()
    val generatedName: String
    val generatedBuilderName: String = "Builder"

    private val _builderClass: KSClassDeclaration?

    val builderClass: KSClassDeclaration
        get() = _builderClass ?: error("builder class not found")

    private val withBuilderConstructorParameters: List<KSValueParameter>
    val builderParameters: List<BuilderParameter>

    val typeParameters: List<KSTypeParameter>

    init {
        val withBuilderClassName = withBuilderClass.simpleName.asString()
        generatedName = "Base${withBuilderClassName}"

        withBuilderConstructorParameters = withBuilderClass.primaryConstructor?.parameters ?: listOf()

        _builderClass = withBuilderClass.declarations
            .filterIsInstance<KSClassDeclaration>()
            .firstOrNull { it.simpleName.asString() == "Builder" }

        builderParameters = _builderClass?.primaryConstructor?.parameters
            ?.map(::BuilderParameter) ?: listOf()

        typeParameters = _builderClass?.typeParameters ?: listOf()
    }

    private fun validateSuperType(
        theClass: KSClassDeclaration,
        expectedSuperTypeName: String,
        expectedSuperTypeQualifier: String? = null
    ): Boolean {
        val superTypeRefs = theClass.superTypes.map { it.element }
            .filterIsInstance<KSClassifierReference>()
        return superTypeRefs.any { ref ->
            ref.referencedName() == expectedSuperTypeName &&
                    expectedSuperTypeQualifier?.let { ref.qualifier?.referencedName() == it } != false
        }
    }

    fun validate(logger: KSPLogger): Boolean {
        if (!validateSuperType(withBuilderClass, generatedName)){
            logger.error("class annotated with 'WithBuilder' must have supertype '$generatedName'", withBuilderClass)
            return false
        }
        if (_builderClass == null || !validateSuperType(builderClass, generatedBuilderName, generatedName)){
            logger.error("class annotated with 'WithBuilder' must have a child class named \"Builder\" with supertype '$generatedBuilderName'", withBuilderClass)
            return false
        }

        if (withBuilderConstructorParameters.singleOrNull()?.type?.resolve()?.declaration?.simpleName?.asString() != "Builder"){
            logger.error("class annotated with 'WithBuilder' must receive only its builder class in its primary constructor", withBuilderClass)
            return false
        }

        return builderParameters.all { it.validate(logger) }
    }

}