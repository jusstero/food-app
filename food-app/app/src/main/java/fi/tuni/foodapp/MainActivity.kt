package fi.tuni.foodapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var searchEditText: EditText
    lateinit var addIngredientButton: Button
    lateinit var listViewOfIngredients: ListView
    lateinit var ingredientTextView: TextView
    lateinit var recipeContainer: LinearLayout
    var ingredientList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.searchEditText = findViewById(R.id.searchEditText)
        this.addIngredientButton = findViewById((R.id.addIngredientButton))
        this.listViewOfIngredients = findViewById((R.id.listViewOfIngredients))
        this.recipeContainer = findViewById(R.id.recipes)


        this.searchEditText.addMyKeyListener {
            addIngredientButton.isEnabled = searchEditText.text.isNotEmpty()
        }
    }


    fun searchRecipes(view: View) {
        val searchWith = ingredientList.joinToString(",+")
        Log.d("TAG", searchWith)

        val key = "MY_KEY"
        val base = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=$key&"
        val args = "ingredients=$searchWith"
        val apiCall : String = base + args
        val url = URL(apiCall)

        Log.d("TAG", url.toString())

        downloadUrlAsync(this, url.toString()) {
            Log.d("TAG", it)
            if (it != null) {
                var listOfRecipes = parseRecipeJson(it)
                Log.d("TAG", listOfRecipes?.toString())
                buildRecipeList(listOfRecipes)

            }
        }

    }


    fun deleteIngredient(view: View) {
        view as TextView
        Log.d("TAG", view.text as String)
    }

    fun addIngredient(view: View) {
        val ingredient = this.searchEditText.text.toString()

        if (ingredient.isNotEmpty()) {
            ingredientList.add(ingredient)
            searchEditText.setText("")
        }
        Log.d("TAG", ingredientList.toString())

        var adapter = ArrayAdapter<String>(this, R.layout.ingredient, R.id.ingredientTextView, ingredientList)
        listViewOfIngredients.adapter = adapter

    }

    fun buildRecipeList(listOfRecipes: MutableList<Recipe>?) {
        recipeContainer.removeAllViews()

        listOfRecipes?.forEach {
            val title = it.title
            val imageUrl = it.image

            if (title !=null && imageUrl != null) {
                val imageView = ImageView(this)
                val textView = TextView(this)

                Picasso.get().load(imageUrl.toString()).into(imageView);
                textView.text = title

                this.recipeContainer.addView(textView)
                this.recipeContainer.addView(imageView)

            }
        }
    }


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
