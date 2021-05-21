package fi.tuni.foodapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import java.net.URL

/**
 * Holds data for recipes given by spoonacular api "Search Recipes By Ingredients" endpoint (https://spoonacular.com/food-api/docs#Search-Recipes-by-Ingredients)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Recipe(
    var id: Int? = null,
    var title: String? = null,
    var image: URL? = null,
    var imageType: String? = null) :Serializable
