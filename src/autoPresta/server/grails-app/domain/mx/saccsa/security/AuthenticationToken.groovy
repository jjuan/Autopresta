package mx.saccsa.security

class AuthenticationToken {

    String username
    String token
    Date refreshed = new Date()
    String sessionId
    static constraints = {
        username unique: true
    }
    static mapping = {
        table "AuthenticationToken"
        version false
        id generator: 'identity'
        sessionId column: "sessionId", name:"sessionId"
    }
    def afterLoad() {
        // if being accessed and it is more than a day since last marked as refreshed
        // and it hasn't been wiped out by Quartz job (it exists, duh)
        // then refresh it

        if (refreshed.time < (new Date().time -1)) {
            executeUpdate("UPDATE AuthenticationToken SET refreshed =:fecha WHERE id=:id",[fecha:new Date(),id:id]);
        }
    }
}
