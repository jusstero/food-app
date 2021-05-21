package fi.tuni.foodapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import java.net.URL
/**
 * Activity class displays information about a recipe that was chosen by the user in RecipeListActivity.
 *
 * User gets nutrient and ingredient information of the recipe, and can open the original recipe url for instructions.
 */
class SingleRecipeActivity : AppCompatActivity() {
    lateinit var recipe: Recipe
    lateinit var recipeObject: RecipeJsonObject
    lateinit var ingredientAdapter : ArrayAdapter<Ingredient>
    lateinit var nutrientAdapter : ArrayAdapter<Nutrient>
    lateinit var listViewRecipeIngredients: ListView
    lateinit var listViewRecipeNutrients: ListView
    lateinit var recipeImage : ImageView
    lateinit var timeToPrepare : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_recipe)

        this.listViewRecipeIngredients = findViewById(R.id.listViewRecipeIngredients)
        this.listViewRecipeNutrients = findViewById(R.id.listViewRecipeNutrients)
        this.recipeImage = findViewById(R.id.recipeImage)
        this.timeToPrepare = findViewById(R.id.timeToPrepare)

        this.recipe = intent.extras?.get("recipe") as Recipe
        title = recipe.title

        getRecipeById(recipe)
    }

    /**
     * Takes a recipe id and makes an api call with the id.
     *
     * After JSON is parsed, [buildRecipeToUi] is called to display the recipe and its nutrient and ingredient information in the UI.
     */
    fun getRecipeById(recipe: Recipe) {
        val apiCallUrl = formRecipeByIdJSONUrl(recipe.id)

        downloadUrlAsync(this, apiCallUrl) {
            if (it != null) {
                recipeObject = parseSingleRecipeJSON(it)
                buildRecipeToUi(recipeObject)
            }
        }
    }

    /**
     * Builds the recipe to the UI.
     */
    private fun buildRecipeToUi(recipeJsonObject: RecipeJsonObject) {
        runOnUiThread {
            displayTimeToPrepare(recipeJsonObject.readyInMinutes)
            displayRecipeImage(recipe.image)
            displayIngredients(recipeJsonObject.nutrition?.ingredients)
            displayNutrients(recipeJsonObject.nutrition?.nutrients)
        }
    }

    private fun displayTimeToPrepare(readyInMinutes: Int?) {
        if (readyInMinutes != null) {
            timeToPrepare.text = "Ready in $readyInMinutes minutes"
        }
    }

    /**
     * Displays the recipe image to the user.
     *
     * Uses Picasso library (https://square.github.io/picasso/) to load and resize recipe images.
     */
    private fun displayRecipeImage(image: URL?) {
        Picasso.get()
            .load(image.toString())
            .resize(556, 370)
            .into(recipeImage);
    }

    /**
     * Displays the ingredients used in the recipe to the user.
     */
    private fun displayIngredients(ingredientList: MutableList<Ingredient>?) {
        ingredientAdapter = ArrayAdapter<Ingredient>(this, R.layout.recipe_property, R.id.recipeProperty, ingredientList as ArrayList)
        listViewRecipeIngredients.adapter = ingredientAdapter
    }


    /**
     * Displays the nutrient information in the recipe to the user.
     */
    private fun displayNutrients(nutrientList: MutableList<Nutrient>?) {
        val nutrientSet = setOf("Calories", "Fat", "Carbohydrates", "Protein")
        val nutrientsForDisplay = nutrientList?.filter { it.name in nutrientSet }

        nutrientAdapter = ArrayAdapter<Nutrient>(this, R.layout.recipe_property, R.id.recipeProperty, nutrientsForDisplay as ArrayList)
        listViewRecipeNutrients.adapter = nutrientAdapter
    }

    /**
     * Opens URL for the recipe.
     *
     * onClick function for Button in activity_single_recipe.xml
     */
    fun openRecipeUrl(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipeObject.sourceUrl))
        startActivity(intent)
    }
}