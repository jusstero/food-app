package fi.tuni.foodapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.net.URL

/**
 * Holds data from recipe JSON given by the spoonacular api "Get Recipe Information" endpoint (https://spoonacular.com/food-api/docs#Get-Recipe-Information)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RecipeJsonObject(
    var readyInMinutes: Int? = null,
    var nutrition: NutritionObject? = null,
    var sourceUrl: String? = null) :Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class NutritionObject(
    var nutrients: MutableList<Nutrient>? = null,
    var ingredients: MutableList<Ingredient>? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Ingredient(
    var name: String? = null,
    var amount: String? = null,
    var unit: String? = null) {

    override fun toString(): String {
        return "$amount $unit $name"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Nutrient(
    var name: String? = null,
    var amount: Double? = null,
    var unit: String? = null, ) {


    override fun toString(): String {
        return "$amount $unit $name"
    }
}