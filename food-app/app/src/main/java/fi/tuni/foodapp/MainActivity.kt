package fi.tuni.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.io.Serializable
import android.util.Log

/**
 * This activity class serves as the entry point for the app.
 *
 * User can enter ingredients to a list via an EditText view.
 * Ingredient list is displayed in the UI.
 *
 * User can delete ingredients from the list by touching an ingredient and pressing yes on the confirmation alert dialog.
 *
 * User can search for recipes using the given ingredients
 *
 * App uses spoonacular api (https://spoonacular.com/food-api)
 */
class MainActivity : AppCompatActivity() {
    lateinit var ingredientEditText: EditText
    lateinit var addIngredientButton: Button
    lateinit var startSearchButton: Button
    lateinit var listViewOfIngredients: ListView
    lateinit var touchToDelete: TextView
    lateinit var recipeContainer: LinearLayout
    var ingredientList = mutableListOf<String>()
    var ingredientListNoWhiteSpaces = mutableListOf<String>()
    lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.ingredientEditText = findViewById(R.id.ingredientEditText)
        this.addIngredientButton = findViewById((R.id.addIngredientButton))
        this.startSearchButton = findViewById(R.id.startSearchButton)
        this.listViewOfIngredients = findViewById((R.id.listViewOfIngredients))
        this.recipeContainer = findViewById(R.id.recipes)
        this.touchToDelete = findViewById(R.id.touchToDelete)

        this.ingredientEditText.addMyKeyListener {
            addIngredientButton.isEnabled = ingredientEditText.text.isNotEmpty()
        }
        this.startSearchButton.isEnabled = false
    }

    /**
     * Searches for recipes using the spoonacular api (https://spoonacular.com/food-api)
     *
     * Used as an onClick function by [startSearchButton]
     */
    fun searchRecipes(view: View) {
        /** Clear whitespaces from ingredient items so they can be used in the api call, e.g. chick pea -> chickpea */
        ingredientListNoWhiteSpaces.clear()
        ingredientList.forEach {
            ingredientListNoWhiteSpaces.add(it.replace("\\s".toRegex(), ""))
        }

        val searchWith = ingredientListNoWhiteSpaces.joinToString(",+")
        val apiCallUrl = formRecipeListJSONUrl(searchWith)

        downloadUrlAsync(this, apiCallUrl) {
            if (it == null) {
                displayToast("Something went wrong.")
            } else {
                displayToast("Searching for recipes...")

                val listOfRecipes = parseRecipeResultsJSON(it)
                if (listOfRecipes != null && listOfRecipes.isEmpty()) {
                    displayToast("No recipes found")
                } else {
                    swapToRecipeListActivity(listOfRecipes)
                }
            }
        }
    }

    /**
     * Changes current activity to RecipeListActivity.
     *
     * Called after [searchRecipes] function completes parsing the recipe JSON.
     */
    fun swapToRecipeListActivity(recipeList: MutableList<Recipe>?) {
        val intent = Intent(this, RecipeListActivity::class.java)
        intent.putExtra("recipeList", recipeList as Serializable)
        startActivity(intent)
    }

    /**
     * Displays an alert dialog to confirm the deletion of an ingredient from the ingredient list UI-element.
     *
     * Used as an onClick function by ingredient list items (ingredient.xml).
     */
    fun confirmIngredientDeletion(view: View) {
        view as TextView
        var ingredient = view.text

        var builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Delete $ingredient from list?")
            setPositiveButton("Yes") { dialog, _ ->
                deleteIngredient(view)
                dialog.cancel()
            }

            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        }
        builder.create().show()
    }

    /**
     * Deletes an ingredient from the [ingredientList] and the UI after user presses yes.
     *
     * in the alert dialog displayed by [confirmIngredientDeletion].
     */
    fun deleteIngredient(view: View) {
        view as TextView
        ingredientList.remove(view.text)

        runOnUiThread {
            adapter.notifyDataSetChanged()
        }

        if (ingredientList.isEmpty()) {
            startSearchButton.isEnabled = false
            touchToDelete.visibility = View.INVISIBLE
        }
    }

    /**
     * Adds the user given ingredient to [ingredientList] and displays said list back to the user.
     *
     * Used as an onClick function by [addIngredientButton].
     */
    fun addIngredient(view: View) {
        var ingredient = this.ingredientEditText.text.toString()

        if (ingredient.isNotEmpty()) {
            ingredientList.add(ingredient)
            ingredientEditText.setText("")
            startSearchButton.isEnabled = true
            touchToDelete.visibility = View.VISIBLE
        }

        adapter = ArrayAdapter<String>(this, R.layout.ingredient, R.id.ingredientTextView, ingredientList)
        listViewOfIngredients.adapter = adapter
    }

    /**
     * Displays a long toast to the screen with the given [toastMessage].
     */
    private fun displayToast(toastMessage: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Text listener used by [ingredientEditText] to determine if [addIngredientButton] is enabled or disabled.
     */
    fun EditText.addMyKeyListener(textChange: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                textChange(s.toString())
            }
        })
    }

}
