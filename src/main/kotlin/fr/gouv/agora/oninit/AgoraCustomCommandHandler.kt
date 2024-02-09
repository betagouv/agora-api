package fr.gouv.agora.oninit

import fr.gouv.agora.AgoraCustomCommandHelper
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
@Suppress("unused")
class AgoraCustomCommandHandler(
    private val applicationContext: ApplicationContext,
    private val dailyTasksHandler: DailyTasksHandler,
    private val weeklyTasksHandler: WeeklyTasksHandler,
    private val deleteUsersHandler: DeleteUsersHandler,
) : InitializingBean {

    companion object {
        private const val DAILY_TASKS = "dailyTasks"
        private const val WEEKLY_TASKS = "weeklyTasks"
        private const val DELETE_USERS = "deleteUsers"
    }

    override fun afterPropertiesSet() {
        if (handleCustomCommand()) {
            SpringApplication.exit(applicationContext, { 0 })
            println("⚙️ Run custom command finished")
        }
    }

    private fun handleCustomCommand(): Boolean {
        return AgoraCustomCommandHelper.getStoredCustomCommandAndClear()?.let { customCommand ->
            println("⚙️ Run custom command = ${customCommand.command} / argument = ${customCommand.arguments}")
            getHandler(customCommand.command)?.handleTask(customCommand.arguments)
            true
        } ?: false
    }

    private fun getHandler(command: String): CustomCommandHandler? {
        return when (command) {
            DAILY_TASKS -> dailyTasksHandler
            WEEKLY_TASKS -> weeklyTasksHandler
            DELETE_USERS -> deleteUsersHandler
            else -> null
        }
    }

}