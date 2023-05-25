package io.github.petretiandrea.mealplanner.web.features.plan.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("request")
interface PlanRequestRestController {

    @PostMapping("/")
    suspend fun createPlanRequest(
        @RequestBody request: CreateMealPlanRequest
    ): ResponseEntity<String>

    @GetMapping("/{requestId}")
    suspend fun getPlanRequest(
        @PathVariable("requestId") requestId: String
    ): ResponseEntity<MealPlanRequestResponse>
}