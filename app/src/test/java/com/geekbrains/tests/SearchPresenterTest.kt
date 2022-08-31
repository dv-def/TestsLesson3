package com.geekbrains.tests

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.model.SearchResult
import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.view.search.ViewSearchContract
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

//Тестируем наш Презентер
class SearchPresenterTest {

    private lateinit var presenter: SearchPresenter

    @Mock
    private lateinit var repository: GitHubRepository

    @Mock
    private lateinit var viewContract: ViewSearchContract

    @Before
    fun setUp() {
        //Обязательно для аннотаций "@Mock"
        //Раньше было @RunWith(MockitoJUnitRunner.class) в аннотации к самому классу (SearchPresenterTest)
        MockitoAnnotations.openMocks(this)
        //Создаем Презентер, используя моки Репозитория и Вью, проинициализированные строкой выше
        presenter = SearchPresenter(repository)
    }

    @Test // Убеждаемся что viewContract не null после вызова onAttach()
    fun onAttachTest() {
        presenter.onAttach(viewContract)
        assertNotNull(presenter.viewContract)
        presenter.onDetach()
    }

    @Test // Убеждаемся что viewContract null после вызова onDetach()
    fun onDetachTest() {
        presenter.onAttach(viewContract)
        assertNotNull(presenter.viewContract)
        presenter.onDetach()
        assertNull(presenter.viewContract)
    }

    @Test //Проверим вызов метода searchGitHub() у нашего Репозитория
    fun searchGitHub_Test() {
        presenter.onAttach(viewContract)
        val searchQuery = "some query"
        //Запускаем код, функционал которого хотим протестировать
        presenter.searchGitHub("some query")
        //Убеждаемся, что все работает как надо
        verify(repository, times(1)).searchGithub(searchQuery, presenter)

        presenter.onDetach()
    }

    @Test //Проверяем работу метода handleGitHubError()
    fun handleGitHubError_Test() {
        presenter.onAttach(viewContract)
        //Вызываем у Презентера метод handleGitHubError()
        presenter.handleGitHubError()
        //Проверяем, что у viewContract вызывается метод displayError()
        verify(viewContract, times(1)).displayError()

        presenter.onDetach()
    }

    //Проверяем работу метода handleGitHubResponse

    @Test //Для начала проверим, как приходит ответ сервера
    fun handleGitHubResponse_ResponseUnsuccessful() {
        presenter.onAttach(viewContract)
        //Создаем мок ответа сервера с типом Response<SearchResponse?>?
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Описываем правило, что при вызове метода isSuccessful должен возвращаться false
        `when`(response.isSuccessful).thenReturn(false)
        //Убеждаемся, что ответ действительно false
        assertFalse(response.isSuccessful)

        presenter.onDetach()
    }

    @Test //Теперь проверим, как у нас обрабатываются ошибки
    fun handleGitHubResponse_Failure() {
        presenter.onAttach(viewContract)
        //Создаем мок ответа сервера с типом Response<SearchResponse?>?
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Описываем правило, что при вызове метода isSuccessful должен возвращаться false
        //В таком случае должен вызываться метод viewContract.displayError(...)
        `when`(response.isSuccessful).thenReturn(false)

        //Вызывваем у Презентера метод handleGitHubResponse()
        presenter.handleGitHubResponse(response)

        //Убеждаемся, что вызывается верный метод: viewContract.displayError("Response is null or unsuccessful"), и что он вызывается единожды
        verify(viewContract, times(1))
            .displayError("Response is null or unsuccessful")

        presenter.onDetach()
    }

    @Test //Проверим порядок вызова методов viewContract
    fun handleGitHubResponse_ResponseFailure_ViewContractMethodOrder() {
        presenter.onAttach(viewContract)

        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.isSuccessful).thenReturn(false)
        presenter.handleGitHubResponse(response)

        //Определяем порядок вызова методов какого класса мы хотим проверить
        val inOrder = inOrder(viewContract)
        //Прописываем порядок вызова методов
        inOrder.verify(viewContract).displayLoading(false)
        inOrder.verify(viewContract).displayError("Response is null or unsuccessful")

        presenter.onDetach()
    }

    @Test //Проверим пустой ответ сервера
    fun handleGitHubResponse_ResponseIsEmpty() {
        presenter.onAttach(viewContract)

        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.body()).thenReturn(null)
        //Вызываем handleGitHubResponse()
        presenter.handleGitHubResponse(response)
        //Убеждаемся, что body действительно null
        assertNull(response.body())

        presenter.onDetach()
    }

    @Test //Теперь проверим непустой ответ сервера
    fun handleGitHubResponse_ResponseIsNotEmpty() {
        presenter.onAttach(viewContract)

        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.body()).thenReturn(mock(SearchResponse::class.java))
        //Вызываем handleGitHubResponse()
        presenter.handleGitHubResponse(response)
        //Убеждаемся, что body действительно null
        assertNotNull(response.body())

        presenter.onDetach()
    }

    @Test //Проверим как обрабатывается случай, если ответ от сервера пришел пустой
    fun handleGitHubResponse_EmptyResponse() {
        presenter.onAttach(viewContract)

        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Устанавливаем правило, что ответ успешный
        `when`(response.isSuccessful).thenReturn(true)
        //При этом body ответа == null. В таком случае должен вызываться метод viewContract.displayError(...)
        `when`(response.body()).thenReturn(null)

        //Вызываем handleGitHubResponse()
        presenter.handleGitHubResponse(response)

        //Убеждаемся, что вызывается верный метод: viewContract.displayError("Search results or total count are null"), и что он вызывается единожды
        verify(viewContract, times(1))
            .displayError("Search results or total count are null")

        presenter.onDetach()
    }

    @Test //Пришло время проверить успешный ответ, так как все остальные случаи мы уже покрыли тестами
    fun handleGitHubResponse_Success() {
        presenter.onAttach(viewContract)

        //Мокаем ответ
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Мокаем тело ответа
        val searchResponse = mock(SearchResponse::class.java)
        //Мокаем список
        val searchResults = listOf(mock(SearchResult::class.java))

        //Прописываем правила
        //Когда ответ приходит, возвращаем response.isSuccessful == true и остальные данные в процессе выполнения метода handleGitHubResponse
        `when`(response.isSuccessful).thenReturn(true)
        `when`(response.body()).thenReturn(searchResponse)
        `when`(searchResponse.searchResults).thenReturn(searchResults)
        `when`(searchResponse.totalCount).thenReturn(101)

        //Запускаем выполнение метода
        presenter.handleGitHubResponse(response)

        //Убеждаемся, что ответ от сервера обрабатывается корректно
        verify(viewContract, times(1)).displaySearchResults(searchResults, 101)

        presenter.onDetach()
    }
}
