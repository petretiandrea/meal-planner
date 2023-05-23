package io.github.petretiandrea.mealplanner

import io.github.petretiandrea.mealplanner.domain.Food
import io.github.petretiandrea.mealplanner.domain.FoodRepository
import java.io.File

class CsvFoodRepository(
    private val sourceFile: File
) : FoodRepository {

    private var foodCache: List<Food> = emptyList()
    private var allLoaded = false

    override fun getFoodByIndex(index: Int): Food? {
        loadAll()
        return foodCache[index]
    }

    override fun getFoodCount(): Int {
        loadAll()
        return foodCache.size
    }

    override fun getAll(): List<Food> {
        loadAll()
        return foodCache
    }

    private fun loadAll() {
        if (!allLoaded) {
            sourceFile.bufferedReader()
                .lines()
                .skip(1)
                .map(this::mapLineToFood)
                .forEach { it?.let { foodCache += it } }
            allLoaded = true
        }
    }

    private fun mapLineToFood(line: String): Food? {
        val pieces = line.split(";")
        if (pieces.size > 3) {
            return Food(
                pieces[0],
                pieces[1].toDouble(),
                pieces[2].toDouble(),
                pieces[3].toDouble(),
                100.0 // by default 100g assumed
            )
        }
        return null
    }
}