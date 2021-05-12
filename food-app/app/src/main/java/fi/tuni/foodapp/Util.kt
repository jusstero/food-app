package fi.tuni.foodapp

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import com.fasterxml.jackson.module.kotlin.*

fun downloadUrlAsync(activity: Activity, s: String, callback: (result : String?) -> Unit) {
    thread() {
        var result = getUrl(s)
        activity.runOnUiThread {
            callback(result)
        }
    }
}

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

fun parseRecipeJson(jsonAsString: String) : MutableList<Recipe>? {
    val mapper = ObjectMapper()
    var recipeList : MutableList<Recipe> = mapper.readValue(jsonAsString)
    return recipeList
}