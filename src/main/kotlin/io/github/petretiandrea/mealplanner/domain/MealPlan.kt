package io.github.petretiandrea.mealplanner.domain

class MealPlan(
    val foods: Set<Food>
) {

    val carbs: Double
        get() = foods.sumOf { it.carbs }

    val fats: Double
        get() = foods.sumOf { it.fats }

    val proteins: Double
        get() = foods.sumOf { it.proteins }

    fun addFood(food: Food): MealPlan {
        return MealPlan(foods + food);
    }

    fun removeFood(food: Food): MealPlan {
        return MealPlan(foods - food);
    }

    override fun toString(): String {
        return "MealPlan(foods=$foods, carbs=$carbs, fats=$fats, proteins=$proteins)"
    }



}