package com.geekbrains.tests.presenter.search

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.repository.GitHubRepository.GitHubRepositoryCallback
import com.geekbrains.tests.view.ViewContract
import com.geekbrains.tests.view.search.ViewSearchContract
import retrofit2.Response

/**
 * В архитектуре MVP все запросы на получение данных адресуются в Репозиторий.
 * Запросы могут проходить через Interactor или UseCase, использовать источники
 * данных (DataSource), но суть от этого не меняется.
 * Непосредственно Презентер отвечает за управление потоками запросов и ответов,
 * выступая в роли регулировщика движения на перекрестке.
 */

internal class SearchPresenter internal constructor(
    private val repository: GitHubRepository
) : PresenterSearchContract, GitHubRepositoryCallback {

    private var _viewContract: ViewSearchContract? = null
    val viewContract: ViewContract? get() = _viewContract

    override fun onAttach(viewContract: ViewContract) {
        this._viewContract = (viewContract as ViewSearchContract)
    }

    override fun onDetach() {
        if (this._viewContract != null) {
            this._viewContract = null
        }
    }

    override fun searchGitHub(searchQuery: String) {
        _viewContract?.displayLoading(true)
        repository.searchGithub(searchQuery, this)
    }

    override fun handleGitHubResponse(response: Response<SearchResponse?>?) {
        _viewContract?.displayLoading(false)
        if (response != null && response.isSuccessful) {
            val searchResponse = response.body()
            val searchResults = searchResponse?.searchResults
            val totalCount = searchResponse?.totalCount
            if (searchResults != null && totalCount != null) {
                _viewContract?.displaySearchResults(
                    searchResults,
                    totalCount
                )
            } else {
                _viewContract?.displayError("Search results or total count are null")
            }
        } else {
            _viewContract?.displayError("Response is null or unsuccessful")
        }
    }

    override fun handleGitHubError() {
        _viewContract?.displayLoading(false)
        _viewContract?.displayError()
    }
}
