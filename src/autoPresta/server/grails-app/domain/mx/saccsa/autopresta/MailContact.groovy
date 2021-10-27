package mx.saccsa.autopresta

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class MailContact {
    String _process
    String domain
    String contactId
    String _to
    String _cc
    String _bcc

    static constraints = {
        _process size:0..100
        domain size:0..100
        contactId size:0..50
        _to size:0..200
        _cc size:0..200, nullable:true, blank:true
        _bcc size:0..200, nullable:true, blank:true
    }
    static mapping = {
        table"MailContact"
        id generator:"identity"
        version false
        _process column:"_process", name:"_process"
        domain column:"domain", name:"domain"
        contactId column:"contactId", name:"contactId"
        _to column:"_to", name:"_to"
        _cc column:"_cc", name:"_cc"
        _bcc column:"_bcc", name:"_bcc"
    }
}
