package mx.saccsa.security

class MAccount {
    String tokenType
    String username
    String token
    Date refreshed = new Date()
    static constraints = {
        tokenType size: 0..20
        username size: 0..100
        token size: 0..300
    }
    static mapping = {
        table "MAccount"
        version false
        id generator: 'identity'
        tokenType column:"tokenType", name:"tokenType"
        username column:"username", name:"username"
        token column:"token", name:"token"
        refreshed column:"refreshed", name:"refreshed"
    }
    def afterLoad() {
        if (refreshed.time < (new Date().time -1)) {
            executeUpdate("UPDATE AuthenticationToken SET refreshed =:fecha WHERE id=:id",[fecha:new Date(),id:id]);
        }
    }
}
