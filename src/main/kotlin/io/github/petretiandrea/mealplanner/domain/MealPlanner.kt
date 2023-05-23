package io.github.petretiandrea.mealplanner.domain

import java.util.stream.Stream

interface MealPlanner {
    fun generatePlan(targetMacro: Macro): Stream<MealPlan>
}