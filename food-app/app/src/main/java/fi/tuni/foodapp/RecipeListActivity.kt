package fi.tuni.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.squareup.picasso.Picasso

/**
 * Activity class displays the recipes that have been found with the user given ingredients.
 *
 * User can touch a recipe image from the list to get more information about a specific recipe.
 */
class RecipeListActivity : AppCompatActivity() {
    lateinit var recipeContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        title = "Recipes"
        this.recipeContainer = findViewById(R.id.recipes)

        val listOfRecipes = intent.extras?.get("recipeList") as MutableList<Recipe>?
        buildRecipeListToUI(listOfRecipes)
    }

    /**
     * Builds a view of the recipes from [listOfRecipes] to the UI.
     *
     * Uses Picasso library (https://square.github.io/picasso/) to load and resize recipe images.
     */
    private fun buildRecipeListToUI(listOfRecipes: MutableList<Recipe>?) {
        runOnUiThread {
            recipeContainer.removeAllViews()

            listOfRecipes?.forEach { recipe ->
                val title = recipe.title
                val imageUrl = recipe.image.toString()

                if (title !=null && imageUrl != null) {
                    val imageView = ImageView(this)
                    val button = Button(this)

                    Picasso.get()
                        .load(imageUrl)
                        .resize(556, 370)
                        .into(imageView);

                    button.text = title
                    button.setOnClickListener {
                        swapToSingleRecipeActivity(recipe)
                    }
                    imageView.setOnClickListener {
                        swapToSingleRecipeActivity(recipe)
                    }

                    this.recipeContainer.addView(imageView)
                    this.recipeContainer.addView(button)

                }
            }
        }
    }

    /**
     * Changes current activity to SingleRecipeActivity
     *
     * Called after the user touches a recipe from the list
     */
    private fun swapToSingleRecipeActivity(recipe: Recipe) {
        val intent = Intent(this, SingleRecipeActivity::class.java)
        intent.putExtra("recipe", recipe)
        startActivity(intent)
    }
}
