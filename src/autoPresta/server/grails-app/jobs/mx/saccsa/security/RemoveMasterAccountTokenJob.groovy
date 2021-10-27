import mx.saccsa.security.MAccount

class RemoveMasterAccountTokenJob {
    final Long EXPIRATION_TIME = 10
    static triggers = {
        cron name: "RemoveMasterAccountTokenJob", cronExpression: "0 0/30 * * * ?"
    }

    def execute() {
        log.debug "Remove token to recover or active account"
        Calendar c = Calendar.getInstance()
        c.timeInMillis = (new Date().time - (60000*EXPIRATION_TIME))
        MAccount.withTransaction {
            MAccount.executeUpdate("delete MAccount a where a.refreshed < :dateTime", [dateTime: c.time])
        }
    }
}
