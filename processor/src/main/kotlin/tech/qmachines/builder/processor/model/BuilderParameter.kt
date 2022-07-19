package tech.qmachines.builder.processor.model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import tech.qmachines.builder.annotation.KeepPropertyType
import tech.qmachines.builder.annotation.MutableProperty
import tech.qmachines.builder.annotation.MutablePropertyType
import tech.qmachines.builder.annotation.TranslateBuilders

class BuilderParameter(
    val parameter: KSValueParameter
){
    private val _immutableType: KSType?
    private val _mutableType: KSType?
    private val _translateBuilders: TranslateBuilders?
    private val _toMutableMethod: String?
    private val _toImmutableMethod: String?

    private val isAnnotated: Boolean

    val immutableType
        get() = _immutableType ?: error("parameter not valid")

    val translateBuilders
        get() = _translateBuilders ?: error("parameter not valid")

    val toMutableMethod
        get() = _toMutableMethod ?: error("parameter not valid")

    val toImmutableMethod
        get() = _toImmutableMethod ?: error("parameter not valid")


    init {
        val mutableAnnotations =
            parameter.getInheritedAnnotationsOf<MutableProperty, MutablePropertyType>()

        isAnnotated = mutableAnnotations.isNotEmpty()

        if (mutableAnnotations.size <= 1) {
            val annotation = mutableAnnotations.firstOrNull()
            _immutableType = annotation?.getArgument<KSType>("immutableClass")?.takeIf {
                it.declaration.qualifiedName?.asString() != KeepPropertyType::class.qualifiedName
            } ?: parameter.type.resolve()
            _mutableType = annotation?.getArgument<KSType>("mutableClass")
            _translateBuilders = annotation?.getArgument<KSType>("translateBuilders")
                ?.declaration?.simpleName?.asString()?.let(TranslateBuilders::valueOf) ?: TranslateBuilders.NEVER
            _toMutableMethod =
                annotation?.getArgument<String>("toMutableMethod")?.takeIf (String::isNotBlank) ?: "%p"
            _toImmutableMethod =
                annotation?.getArgument<String>("toImmutableMethod")?.takeIf (String::isNotBlank) ?: "%p"
        } else {
            _immutableType = null
            _mutableType = null
            _translateBuilders = null
            _toMutableMethod = null
            _toImmutableMethod = null
        }
    }

    fun validate(logger: KSPLogger): Boolean{
        if (_immutableType == null || _toMutableMethod == null || _toImmutableMethod == null) {
            logger.error("only one mutable annotation is allowed per property", parameter)
            return false
        }
        if (!parameter.isVal && !parameter.isVar){
            logger.error("all primary constructor parameters in builder class should be val/var", parameter)
            return false
        }
        if (parameter.isVal && !isAnnotated){
            logger.error("all 'val' arguments in primary constructor should be annotated with '*Property' annotation", parameter)
            return false
        }
        if (_mutableType?.isAssignableFrom(parameter.type.resolve()) == false){
            logger.error("this annotation is not supported for the specified parameter type", parameter)
            return false
        }
        return true
    }

    fun getName() = parameter.name?.asString().orEmpty()
}