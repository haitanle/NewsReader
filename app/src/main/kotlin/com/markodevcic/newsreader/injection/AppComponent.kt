package com.markodevcic.newsreader.injection

import com.markodevcic.newsreader.StartupActivity
import com.markodevcic.newsreader.articles.ArticlesActivity
import com.markodevcic.newsreader.categories.SelectCategoriesActivity
import dagger.Component

@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

	fun inject(startupActivity: StartupActivity)
	fun inject(articlesActivity: ArticlesActivity)
	fun inject(selectCategoriesActivity: SelectCategoriesActivity)
}