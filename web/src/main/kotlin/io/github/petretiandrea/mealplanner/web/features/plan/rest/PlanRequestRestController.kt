package io.github.petretiandrea.mealplanner.web.features.plan.rest

import io.github.petretiandrea.mealplanner.web.features.plan.rest.dto.CreateMealPlan
import io.github.petretiandrea.mealplanner.web.features.plan.rest.dto.MealPlanResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("request")
interface PlanRequestRestController {

    @PostMapping("/")
    suspend fun createPlanRequest(
        @RequestBody request: CreateMealPlan
    ): ResponseEntity<String>

    @GetMapping("/{requestId}")
    suspend fun getPlanRequest(
        @PathVariable("requestId") requestId: String
    ): ResponseEntity<MealPlanResponse>

    @PostMapping("/plan")
    suspend fun createPlanSync(
        @RequestBody request: CreateMealPlan
    ): ResponseEntity<MealPlanResponse>
}