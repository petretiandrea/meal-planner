package io.github.petretiandrea.mealplanner.domain

interface FoodRepository {
    fun getFoodByIndex(index: Int): Food?
    fun getFoodCount(): Int
    fun getAll(): List<Food>
}