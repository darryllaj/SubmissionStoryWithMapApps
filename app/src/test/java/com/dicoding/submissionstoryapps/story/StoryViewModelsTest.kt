package com.dicoding.submissionstoryapps.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.submissionstoryapps.DataDummy
import com.dicoding.submissionstoryapps.MainDispatcherRule
import com.dicoding.submissionstoryapps.Response.ListStoryItem
import com.dicoding.submissionstoryapps.getOrAwaitValue
import com.dicoding.submissionstoryapps.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelsTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule =MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }
    @Test
    fun `when getStory Not Null and Return Data`() = runTest {
        val dummyData = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyData)
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data // Set nilai LiveData dengan data yang valid
        expectedQuote.value = data
        Mockito.`when`(repository.getStory()).thenReturn(expectedQuote)

        val mainViewModel = StoryViewModels(repository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.listStory.getOrAwaitValue()

        // Continue with the rest of your assertions
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyData.size, differ.snapshot().size)
        Assert.assertEquals(dummyData[0], differ.snapshot()[0])
    }


    @Test
    fun `whet getStory Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito.`when`(repository.getStory()).thenReturn(expectedQuote)
        val mainViewModel = StoryViewModels(repository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.listStory.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
class StoryPagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>(){
    companion object{
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(),0,1)
    }
}