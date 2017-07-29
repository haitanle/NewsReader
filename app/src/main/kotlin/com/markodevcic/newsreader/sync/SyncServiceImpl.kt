package com.markodevcic.newsreader.sync

import android.content.SharedPreferences
import com.markodevcic.newsreader.api.NewsApi
import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import com.markodevcic.newsreader.extensions.awaitAll
import com.markodevcic.newsreader.extensions.executeAsync
import com.markodevcic.newsreader.extensions.launchAsync
import com.markodevcic.newsreader.storage.Repository
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.*
import javax.inject.Provider

class SyncServiceImpl(private val newsApi: NewsApi,
					  private val sourcesRepository: Provider<Repository<Source>>,
					  private val articlesRepository: Provider<Repository<Article>>,
					  private val sharedPreferences: SharedPreferences) : SyncService {

	override suspend fun downloadSourcesAsync(categories: Collection<String>): Collection<Source> {
		sourcesRepository.get().use { repo ->
			repo.deleteAll()
			val downloadJobs = categories.map { cat -> newsApi.getSources(cat).launchAsync() }
			val sources = downloadJobs.awaitAll().flatMap { job -> job.sources }
			repo.addAll(sources)
			return sources
		}
	}

	override suspend fun downloadArticlesAsync(source: Source) {
		val response = newsApi.getArticles(source.id).executeAsync()
		response.articles.forEach { article -> article.category = source.category }
		async(CommonPool) {
			val repo = articlesRepository.get()
			repo.use { repo ->
				for (article in response.articles) {
					if (repo.getById(article.url) == null) {
						if (article.publishedAt == null) {
							article.publishedAt = Date().time
						}
						repo.add(article)
					}
				}
			}
		}.await()
	}

	companion object {
		private const val THREE_DAYS_IN_MILISECONDS = 3 * 24 * 60 * 60 * 1000
	}
}

interface SyncService {
	suspend fun downloadSourcesAsync(categories: Collection<String>): Collection<Source>
	suspend fun downloadArticlesAsync(source: Source)
}

class CallSyncService(private val syncService: SyncServiceImpl) {
	fun callDownloadArticlesAsync(source: Source) = runBlocking { syncService.downloadArticlesAsync(source) }
}