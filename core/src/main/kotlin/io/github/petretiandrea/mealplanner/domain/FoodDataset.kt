package io.github.petretiandrea.mealplanner.domain

data class FoodWithConstraint(
    val food: Food,
    val constraint: FoodConstraint? = null
)

interface FoodDataset {
    operator fun get(index: Int): Food?
    fun getConstraint(index: Int): FoodConstraint?
    fun getWithConstraint(index: Int): FoodWithConstraint?
    fun getAll(): List<Food>
    val size: Int

    companion object {
        fun of(vararg foodWithConstraint: FoodWithConstraint): FoodDataset =
            MemoryFoodDataset(foodWithConstraint.toList())

        fun of(foodsWithConstraint: List<FoodWithConstraint>): FoodDataset =
            MemoryFoodDataset(foodsWithConstraint)


        private class MemoryFoodDataset(
            private val foods: List<FoodWithConstraint>
        ) : FoodDataset {
            override fun get(index: Int): Food? =
                foods.getOrNull(index)?.food

            override fun getConstraint(index: Int): FoodConstraint? =
                foods.getOrNull(index)?.constraint

            override fun getWithConstraint(index: Int): FoodWithConstraint? =
                foods.getOrNull(index)

            override fun getAll(): List<Food> = foods.map { it.food }

            override val size: Int
                get() = foods.size

        }
    }
}