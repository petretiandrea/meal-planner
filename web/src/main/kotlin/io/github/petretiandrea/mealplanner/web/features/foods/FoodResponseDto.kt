package io.github.petretiandrea.mealplanner.web.features.foods

data class FoodResponseDto(
    val foodId: String,
    val name: String,
    val carbs: Double,
    val proteins: Double,
    val fats: Double,
    val weight: Double
)
