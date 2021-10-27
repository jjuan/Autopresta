package mx.saccsa.marshaller

import grails.converters.JSON
import grails.converters.XML

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MarshallerUtils {

    static  registerMarshaller() {
        final dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
        final isoDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        // register marshalling logic for both XML and JSON converters
        [XML, JSON].each { converter ->

            // This overrides the marshaller from the java.time to
            // force all DateTime instances to use the UTC time zone
            // and the ISO standard "yyyy-mm-ddThh:mm:ssZ" format
            converter.registerObjectMarshaller(ZonedDateTime, 10) { ZonedDateTime it ->
                return it == null ? null : it.toString(dateTimeFormatter)
            }
            converter.registerObjectMarshaller(Timestamp, 10) { Timestamp it ->
                return it == null ? null : isoDateFormat.format(it)
            }
            converter.registerObjectMarshaller(Date, 10){ Date it ->
                return it == null ? null : isoDateFormat.format(it)
            }
        }
    }
}
