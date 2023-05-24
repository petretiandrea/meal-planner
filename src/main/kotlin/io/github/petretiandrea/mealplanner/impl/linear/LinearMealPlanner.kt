package io.github.petretiandrea.mealplanner.impl.linear

import io.github.petretiandrea.mealplanner.domain.*
import org.apache.commons.math3.optim.MaxIter
import org.apache.commons.math3.optim.linear.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import java.util.stream.IntStream
import java.util.stream.Stream


class LinearMealPlanner(
    private val foods: List<Food>
) : MealPlanner {


    override fun generatePlans(targetMacro: Macro): Stream<MealPlan> {
        val proteins = foods.map { it.proteins }.toDoubleArray()
        val fats = foods.map { it.fats }.toDoubleArray()
        val carbs = foods.map { it.carbs }.toDoubleArray()

        val objective = LinearObjectiveFunction(
            IntStream.range(0, foods.size).mapToDouble { 0.0 }.toArray(),
            0.0
        )
        val constraints = LinearConstraintSet(
            LinearConstraint(proteins, Relationship.LEQ, targetMacro.proteins),
            LinearConstraint(fats, Relationship.LEQ, targetMacro.fats),
            LinearConstraint(carbs, Relationship.LEQ, targetMacro.carbs),
            LinearConstraint(doubleArrayOf(1.0, 0.0, 0.0), Relationship.LEQ, 500.0),
            LinearConstraint(doubleArrayOf(0.0, 1.0, 0.0), Relationship.LEQ, 500.0),
            LinearConstraint(doubleArrayOf(0.0, 0.0, 1.0), Relationship.LEQ, 500.0)
        )

        val solution = SimplexSolver().optimize(
            MaxIter(100), objective, constraints,
            GoalType.MAXIMIZE, NonNegativeConstraint(true)
        )
        val quantities = solution.point

        println(solution)
        quantities.forEach { println(it) }

        return Stream.empty()
    }
}