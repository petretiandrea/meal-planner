package io.github.petretiandrea.mealplanner.domain.impl.linear

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import io.github.petretiandrea.mealplanner.domain.*
import org.apache.commons.math3.optim.linear.*
import java.util.*
import java.util.stream.Stream


class LinearMealPlanner(
    private val foods: List<Food>
) : MealPlanner {

    private val rnd = Random()

    companion object {
        const val INF = Double.POSITIVE_INFINITY
    }

    private fun optimize(foods: List<Food>, targetMacro: Macro) {
        val solver = MPSolver("food", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING)

        val foodVariables = emptyList<MPVariable>().toMutableList()
        foods.forEachIndexed { index, food ->
            // no consider discrete food quantity
            val quantityLowLimit = rnd.nextInt(1, 4).toDouble()
            val quantityUpperLimit = 500.0
            foodVariables += solver.makeNumVar(quantityLowLimit, quantityUpperLimit, "${food.name}_${index}")
        }

        val foodMacroCarbs = foodVariables

        // carbs
//        val totalCarbsMacro =
//            foodVariables.mapIndexed { i, variable -> foods.elementAt(i).carbs * variable.solutionValue() }.sum()
//        val totalMacroVar = solver.makeConstraint(0.0, INF, "total_carbs")
//            .setCoefficient(totalCarbsMacro, 1.0)
//
//        val lowPositive = solver.makeNumVar(0.0, INF, "over_lower_limit_carbs")
//        val lowNegative = solver.makeNumVar(0.0, INF, "under_lower_limit_carbs")
//        solver.makeConstraint().

        // Create the sum: sum_i food_i * macro_i
//        MPVariable totalMacro = solver.makeNumVar(0, INF, "total_" + macro);
//        for (int i = 0; i < day.length; i++) {
//            solver.makeConstraint(0, INF, foodMacros[i] * solution.get(i)).setCoefficient(totalMacro, 1);
//        }
//
//        // Slack variables related to the lower limit
//        MPVariable lowPositive = solver.makeNumVar(0, INF, "over_lower_limit_" + macro);
//        MPVariable lowNegative = solver.makeNumVar(0, INF, "under_lower_limit_" + macro);
//        solver.makeConstraint(limits[0], limits[0], totalMacro).setCoefficient(lowPositive, 1);
//        solver.makeConstraint(-limits[0], -limits[0], totalMacro).setCoefficient(lowNegative, -1);

    }

    private fun generateRandomSample(foods: List<Food>, numberOfMeals: Int = 3): List<Food> {
        val mutable = foods.toMutableSet()
        val samples = mutableListOf<Food>()
        for (i in 0 until numberOfMeals) {
            val sample = foods.random()
            samples.add(sample)
            mutable.remove(sample)
        }

        return samples
    }

    override fun generatePlans(dataset: FoodDataset, targetMacro: Macro): Stream<MealPlan> {
        TODO("Not yet implemented")
    }
}