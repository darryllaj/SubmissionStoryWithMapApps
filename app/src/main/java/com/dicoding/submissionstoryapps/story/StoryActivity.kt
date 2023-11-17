package com.dicoding.submissionstoryapps.story

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionstoryapps.R
import com.dicoding.submissionstoryapps.Response.ListStoryItem
import com.dicoding.submissionstoryapps.activity.DetailActivity
import com.dicoding.submissionstoryapps.activity.LoginActivity
import com.dicoding.submissionstoryapps.databinding.ActivityStoryBinding
import com.dicoding.submissionstoryapps.maps.MapsActivity
import com.dicoding.submissionstoryapps.viewModels.ViewModelFactory

class StoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<StoryViewModels> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStory()

        binding.floatAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    viewModel.logout()
                    AlertDialog.Builder(this).apply {
                        setTitle("Selamat Tinggal!")
                        setMessage("Anda berhasil Logout. Sampai Jumpa Lagi :D.")
                        setPositiveButton("Ok") { _, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                    true
                }
                R.id.menu2 -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }
    }
    private fun setStory(){
        val adapter = StoryAdapter()
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        viewModel.listStory.observe(this){
            adapter.submitData(lifecycle,it)
        }
        binding.rvListStory.setHasFixedSize(true)
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = adapter
        adapter.setOnItemCallback(object : StoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListStoryItem) {
                Intent(this@StoryActivity, DetailActivity::class.java).also {
                    intent ->
                    intent.putExtra(DetailActivity.NAME, data.name)
                    intent.putExtra(DetailActivity.IMG, data.photoUrl)
                    intent.putExtra(DetailActivity.DATE, data.createdAt)
                    intent.putExtra(DetailActivity.DESC, data.description)
                    startActivity(intent)
                }
            }
        })
    }
}