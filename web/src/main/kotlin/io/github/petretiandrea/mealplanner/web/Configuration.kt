package io.github.petretiandrea.mealplanner.web

import io.github.petretiandrea.mealplanner.web.features.plan.domain.MealPlanJobDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Configuration {

    @Bean
    fun mealJobDispatcher(): MealPlanJobDispatcher {
        return MealPlanJobDispatcher.default(
            CoroutineScope(Dispatchers.IO)
        )
    }

}