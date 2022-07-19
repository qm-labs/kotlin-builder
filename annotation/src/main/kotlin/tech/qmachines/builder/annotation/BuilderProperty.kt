package tech.qmachines.builder.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MutablePropertyType(
    MutableProperty(
        immutableClass = KeepPropertyType::class,
        translateBuilders = TranslateBuilders.ONLY_TYPE,
        toMutableMethod = "%p.builder()",
        toImmutableMethod = "%p.build()"
    )
)
annotation class BuilderProperty