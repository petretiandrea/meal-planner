package io.github.petretiandrea.mealplanner.domain

import java.util.stream.Stream

interface MealPlanner {
    fun generatePlans(targetMacro: Macro): Stream<MealPlan>
}