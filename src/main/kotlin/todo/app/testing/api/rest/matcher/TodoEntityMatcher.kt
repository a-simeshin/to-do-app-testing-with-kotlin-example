package todo.app.testing.api.rest.matcher

import org.hamcrest.Description
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import todo.app.testing.api.rest.dto.TodoEntity

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
