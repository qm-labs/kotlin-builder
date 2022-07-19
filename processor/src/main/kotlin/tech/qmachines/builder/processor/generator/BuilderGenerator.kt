package tech.qmachines.builder.processor.generator

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import tech.qmachines.builder.annotation.TranslateBuilders
import tech.qmachines.builder.annotation.WithBuilder
import tech.qmachines.builder.processor.model.BuilderParameter
import tech.qmachines.builder.processor.model.Model
import tech.qmachines.builder.processor.model.getAnnotationOf

internal object BuilderGenerator {

    private const val newLine = "\n"

    fun generate(model: Model) = FileSpec.scriptBuilder(model.generatedName, model.packageName)
        .generateBaseClass(model)
        .build()

    private fun FileSpec.Builder.generateBaseClass(model: Model): FileSpec.Builder = apply {
        addType(
            TypeSpec.classBuilder(model.generatedName).apply {
                addTypeVariables(model.typeParameters.map { it.toTypeVariableName() })
                addModifiers(getVisibility(model), KModifier.ABSTRACT)
                primaryConstructor(generateBaseClassConstructor(model))
            }.build()
        )
        addCode("{\n")
        addCode("⇥")
        addCode(newLine)

        model.builderParameters.forEach { parameter ->
            addCode(generateBaseClassProperty(parameter))
        }

        addFunction(generateBaseClassBuilderFunction(model))
        addCode(newLine)
        addFunction(generateBaseClassEqualsFunction(model))
        addCode(newLine)
        addFunction(generateBaseClassHashCodeFunction(model))
        addCode(newLine)

        addType(generateBuilderClass(model))
        addCode(newLine)

        addCode("⇤")
        addCode("}\n")
    }

//    private fun generateBaseClass(model: Model) = TypeSpec.classBuilder(model.generatedName).apply {
//        addTypeVariables(model.typeParameters.map { it.toTypeVariableName() })
//        addModifiers(getVisibility(model), KModifier.ABSTRACT)
//
//        primaryConstructor(generateBaseClassConstructor(model))
//
//        model.builderParameters.forEach { parameter ->
//            addProperty(generateBaseClassProperty(parameter))
//        }
//
//        addFunction(generateBaseClassBuilderFunction(model))
//
//        addFunction(generateBaseClassEqualsFunction(model))
//
//        addFunction(generateBaseClassHashCodeFunction(model))
//
//        addType(generateBuilderClass(model))
//
//    }.build()

    private fun generateBaseClassConstructor(model: Model) = FunSpec.constructorBuilder()
        .addParameter(
            "builder",
            model.builderClass.toParametrizedClassName(model.typeParameters.map { it.toTypeVariableName() })
        )
        .build()

//    private fun generateBaseClassProperty(parameter: BuilderParameter): PropertySpec {
//        val typeArguments = parameter.parameter.type.resolve().arguments.mapNotNull {
//            if (parameter.translateBuilders == TranslateBuilders.TYPE_AND_TYPE_ARGUMENTS)
//                it.type?.resolve()?.stripBuilderClass()?.toTypeName()
//            else
//                it.type?.toTypeName()
//        }
//        val type = parameter.immutableType.let {
//            if (parameter.translateBuilders != TranslateBuilders.NEVER) it.stripBuilderClass() else it
//        }.declaration.toParametrizedClassName(typeArguments)
//            .copy(nullable = parameter.immutableType.isMarkedNullable)
//        return PropertySpec.builder(parameter.getName(), type)
//            .initializer(parameter.toImmutableMethod.let { Formatter.format(it, property = "builder.${parameter.getName()}") })
//            .build()
//    }

    private fun generateBaseClassProperty(parameter: BuilderParameter): CodeBlock {
        val typeArguments = parameter.parameter.type.resolve().arguments.mapNotNull {
            if (parameter.translateBuilders == TranslateBuilders.TYPE_AND_TYPE_ARGUMENTS)
                it.type?.resolve()?.stripBuilderClass()?.toTypeName()
            else
                it.type?.toTypeName()
        }
        val type = parameter.immutableType.let {
            if (parameter.translateBuilders != TranslateBuilders.NEVER) it.stripBuilderClass() else it
        }.declaration.toParametrizedClassName(typeArguments)
            .copy(nullable = parameter.immutableType.isMarkedNullable)
        return CodeBlock.builder()
            .add("val %N", parameter.getName())
            .apply { if (parameter.translateBuilders != TranslateBuilders.INFER) add(": %T", type) }
            .add(" = ")
            .add(parameter.toImmutableMethod.let { Formatter.format(it, property = "builder.${parameter.getName()}") })
            .add(newLine)
            .add(newLine)
            .build()
    }

    private fun generateBaseClassBuilderFunction(model: Model) = FunSpec.builder("builder")
        .addStatement(
            "return %T(\n${
                model.builderParameters.joinToString(",\n") { parameter ->
                    "  ${parameter.getName()} = ${parameter.toMutableMethod.let { Formatter.format(it, property = parameter.getName()) }}"
                }
            }\n)",
            model.builderClass.toParametrizedClassName(model.typeParameters.map { it.toTypeVariableName() })
        )
        .build()

    private fun generateBaseClassEqualsFunction(model: Model) = FunSpec.builder("equals")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("other", Any::class.asTypeName().copy(nullable = true))
        .returns(Boolean::class)
        .addStatement("if (this === other) return true")
        .addStatement("if (javaClass != other?.javaClass) return false")
        .addStatement("other as ${model.generatedName}${wildcardsTypeArguments(model.typeParameters.size)}")
        .apply {
            model.builderParameters.forEach { parameter ->
                addStatement("if (${parameter.getName()} != other.${parameter.getName()}) return false")
            }
        }
        .addStatement("return true")
        .build()

    private fun generateBaseClassHashCodeFunction(model: Model) = FunSpec.builder("hashCode")
        .addModifiers(KModifier.OVERRIDE)
        .returns(Int::class)
        .addStatement("var result = ${model.builderParameters.firstOrNull()?.getName() ?: "0"}.hashCode()")
        .apply {
            model.builderParameters.drop(1).forEach { parameter ->
                addStatement("result = 31 * result + ${parameter.getName()}.hashCode()")
            }
        }
        .addStatement("return result")
        .build()

    private fun generateBuilderClass(model: Model) = TypeSpec.classBuilder(model.generatedBuilderName).apply {
        addTypeVariables(model.typeParameters.map { it.toTypeVariableName() })
        addModifiers(getVisibility(model), KModifier.ABSTRACT)

        addFunction(
            generateBuilderClassBuildFunction(model)
        )
    }.build()

    private fun generateBuilderClassBuildFunction(model: Model) = FunSpec.builder("build")
        .addStatement(
            "return %T(this as %T)",
            model.withBuilderClass.toClassName(),
            model.builderClass.toParametrizedClassName(model.typeParameters.map { it.toTypeVariableName() })
        )
        .build()

    private fun getVisibility(model: Model) =
        if (model.withBuilderClass.modifiers.intersect(setOf(Modifier.INTERNAL, Modifier.PRIVATE, Modifier.PROTECTED)).isEmpty())
            KModifier.PUBLIC
        else
            KModifier.INTERNAL

    private fun wildcardsTypeArguments(number: Int) =
        if (number<=0) "" else (0 until number).joinToString(", ", "<", ">") { "*" }

    private fun KSType.stripBuilderClass(): KSType {
        val parent = declaration.parent as? KSClassDeclaration ?: return this
        val isBuilder =
            parent.getAnnotationOf<WithBuilder>().isNotEmpty() && declaration.simpleName.asString() == "Builder"
        if (!isBuilder) return this
        return parent.asType(arguments)
    }
}