package io.github.petretiandrea.mealplanner.web.features.plan.domain

import io.github.petretiandrea.mealplanner.domain.Macro

data class MealPlanJobRequest(
    val targetMacro: Macro,
    val numberOfPlans: Int,
    val foodDataset: FoodDataset
)