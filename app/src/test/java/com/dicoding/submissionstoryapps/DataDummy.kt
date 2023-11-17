package com.dicoding.submissionstoryapps

import com.dicoding.submissionstoryapps.Response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val storyList = ListStoryItem(
                i.toString(),
                "Created $i",
            "name + $i",

            )
            items.add(storyList)
        }
        return items
    }
}