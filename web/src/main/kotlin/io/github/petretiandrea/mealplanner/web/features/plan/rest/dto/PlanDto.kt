package io.github.petretiandrea.mealplanner.web.features.plan.rest.dto

import io.github.petretiandrea.mealplanner.web.features.foods.FoodResponseDto

data class PlanDto(
    val foods: List<FoodResponseDto>,
    val carbs: Double,
    val proteins: Double,
    val fats: Double,
    val kCal: Double
)
