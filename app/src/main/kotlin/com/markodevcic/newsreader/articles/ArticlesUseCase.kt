package com.markodevcic.newsreader.articles

import com.markodevcic.newsreader.data.Article
import com.markodevcic.newsreader.data.Source
import java.io.Closeable

interface ArticlesUseCase : Closeable {
	fun hasArticles(): Boolean
	fun getUnreadCount(categories: Collection<String>): Map<String, Long>

	suspend fun getArticlesAsync(category: String?): List<Article>
	suspend fun getSourcesAsync(category: String?, selectedCategories: Collection<String>): List<Source>
	suspend fun onCategoriesChangedAsync(deletedCategories: Collection<String>)
	suspend fun deleteOldArticlesAsync(daysToDelete: Int)
	suspend fun markArticleReadReadAsync(vararg url: String)
	suspend fun markArticlesUnreadAsync(vararg url: String)
}