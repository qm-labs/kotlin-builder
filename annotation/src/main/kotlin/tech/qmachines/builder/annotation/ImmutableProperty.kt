package tech.qmachines.builder.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MutablePropertyType(
    MutableProperty(
        immutableClass = KeepPropertyType::class,
        translateBuilders = TranslateBuilders.NEVER
    )
)
annotation class ImmutableProperty