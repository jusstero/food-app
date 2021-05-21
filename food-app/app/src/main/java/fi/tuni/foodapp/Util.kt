package fi.tuni.foodapp

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import com.fasterxml.jackson.module.kotlin.*

/**
 * Calls for [getUrl] in a new thread and calls for the given callback function.
 */
fun downloadUrlAsync(activity: Activity, s: String, callback: (result : String?) -> Unit) {
    thread() {
        var result = getUrl(s)
        callback(result)
    }
}

/**
 * Reads data from the given [url] and returns it as a string
 */
fun getUrl(url: String) : String? {
    var myUrl = URL(url)
    var conn = myUrl.openConnection() as HttpURLConnection

    var result = ""

    var inputStream = conn.inputStream
    var reader = BufferedReader(InputStreamReader(conn.inputStream))

    inputStream.use {
        var line = reader.readLine()
        while(line != null) {
            result += line
            line = reader.readLine()
        }
    }

    return result
    //inputStream.close()

}

/**
 * Parses the recipe list JSON into a MutableList full of Recipe objects and returns it.
 */
fun parseRecipeResultsJSON(jsonAsString: String) : MutableList<Recipe>? {
    val mapper = ObjectMapper()
    return mapper.readValue(jsonAsString)
}

/**
 * Parses the recipe json into a RecipeJsonObject and returns it.
 */
fun parseSingleRecipeJSON(jsonAsString: String) : RecipeJsonObject {
    val mapper = ObjectMapper()
    return mapper.readValue(jsonAsString, RecipeJsonObject::class.java)
}

/**
 * Forms url for recipe list api call using the [ingredientsString] that is formed from user given ingredients.
 */
fun formRecipeListJSONUrl(ingredientsString: String) : String {
    val key = "MYKEY"
    val base = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=$key&"
    val args = "ingredients=$ingredientsString"
    return base + args

}

/**
 * Forms url for single recipe api call.
 */
fun formRecipeByIdJSONUrl(recipeId: Int?) : String {
    val key = "MYKEY"
    val base = "https://api.spoonacular.com/recipes/$recipeId/information?apiKey=$key&"
    val args = "includeNutrition=true"
    return base + args
}
