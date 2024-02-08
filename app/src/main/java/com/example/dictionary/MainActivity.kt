package com.example.dictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dictionary.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    lateinit var adapter: MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener{
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = adapter
    }

    private fun getMeaning(word : String) {
        setInprogress(true)
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body()==null){
                    throw (Exception())
                }
                runOnUiThread{
                    setInprogress(false)
                    response.body()?.first().let {
                        if (it != null) {
                            setUI(it)
                        }
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    setInprogress(false)
                    Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    private fun setUI(response: WordResult) {
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)
    }

    private fun setInprogress(inProgress : Boolean) {
        if(inProgress) {
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }else {
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}