package todo.app.testing.api.rest.matcher

import org.hamcrest.Description
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import todo.app.testing.api.rest.dto.TodoEntity

/**
 * Custom Matcher implementation for matching a Collection of TodoEntity objects with an expected
 * TodoEntity. This matcher checks if the collection contains an item that matches the specified
 * TodoEntity based on its ID, text, and completed status.
 *
 * @param expecting The TodoEntity object to be matched against the collection.
 */
class TodoEntityResponseMatcher(expecting: TodoEntity) : TypeSafeMatcher<Collection<TodoEntity>>() {

    private val matcher =
        anyOf(
            hasItem<TodoEntity>(
                allOf(
                    hasProperty("id", equalTo(expecting.id)),
                    hasProperty("text", equalTo(expecting.text)),
                    hasProperty("completed", equalTo(expecting.completed))
                )
            )
        )

    override fun matchesSafely(item: Collection<TodoEntity>): Boolean {
        return matcher.matches(item)
    }

    override fun describeTo(description: Description) {
        matcher.describeTo(description)
    }
}
