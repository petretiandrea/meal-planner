package io.github.petretiandrea.mealplanner.cli

import io.github.petretiandrea.mealplanner.cli.Constants.DEFAULT_FOOD_FILE
import io.github.petretiandrea.mealplanner.cli.Constants.DEFAULT_PROPOSED_PLANS
import io.github.petretiandrea.mealplanner.domain.Macro
import io.github.petretiandrea.mealplanner.cli.impl.CsvFoodRepository
import io.github.petretiandrea.mealplanner.domain.impl.genetic.GeneticMealPlanner
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.io.File
import java.util.logging.Level
import java.util.logging.LogManager


fun main(args: Array<String>) {

    val parser = ArgParser("meal-planner")
    val debug by parser.option(ArgType.Boolean, shortName = "d", description = "Debug log").default(false)

    val targetCarbs by parser.option(ArgType.Double, shortName = "carb", description = "Target Carbs").required()
    val targetFats by parser.option(ArgType.Double, shortName = "fat", description = "Target Fats").required()
    val targetProteins by parser.option(ArgType.Double, shortName = "pro", description = "Target Proteins").required()

    val plans by parser.option(ArgType.Int, shortName = "plans", description = "Number of plans")
        .default(DEFAULT_PROPOSED_PLANS)
    val foodFile by parser.option(ArgType.String, shortName = "foods", description = "Foods file")
        .default(DEFAULT_FOOD_FILE)

    parser.parse(args)

    updateLogLevel(if (debug) Level.FINE else Level.INFO)

    val foodDataset = CsvFoodRepository(File(foodFile))
    val targetMacros = Macro(targetCarbs, targetProteins, targetFats)

    val mealPlanner = GeneticMealPlanner()
    val meals = mealPlanner.generatePlans(foodDataset, targetMacros)

    println("Target Macro $targetMacros")
    meals.limit(plans.toLong()).forEach { plan ->
        MealPlanPrinter.printPlan(plan)
        println("---------------------------------")
    }
}

private fun updateLogLevel(logLevel: Level) {
    LogManager.getLogManager().getLogger("").apply {
        level = logLevel
        handlers.forEach { it.level = logLevel }
    }
}