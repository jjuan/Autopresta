package mx.saccsa.security

import groovy.util.logging.Slf4j

@Slf4j
class RemoveStaleTokensJob {

    static triggers = {
        cron name: "cleanAuth", cronExpression: "0 0/10 * * * ?"
    }

    def execute() {
        log.debug "CRON DELETE AuthenticationToken"
        Calendar c = Calendar.getInstance()
        c.timeInMillis = (new Date().time - 18000000)
        AuthenticationToken.withTransaction {
            AuthenticationToken.executeUpdate("delete AuthenticationToken a where a.refreshed < :dateTime", [dateTime: c.getTime()])
        }
    }
}
