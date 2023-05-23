package io.github.petretiandrea.mealplanner

import io.github.petretiandrea.mealplanner.genetic.GeneticMealPlanner
import java.io.File

fun main() {

    val foodRepository = CsvFoodRepository(
        File("/Users/andrea.petreti/Development/meal-planner/src/main/resources/foods.csv")
    )
    val foods = foodRepository.getAll()

    foods.forEach { println(it.ofGrams(50.0)) }

    val mealPlanner = GeneticMealPlanner(foods)

    val meals = mealPlanner.generatePlan(
        40.0, 15.0, 10.0
    )

    meals.forEach { meal ->
        println("-------- PLAN -------")
        println("FAT: ${meal.fats} g, CARBS: ${meal.carbs} g PRO: ${meal.proteins} g, ${meal.foods.sumOf { it.kCal }} kCal")
        meal.foods.forEach {
            println("${it.name}, ${it.grams} g")
        }
    }
}