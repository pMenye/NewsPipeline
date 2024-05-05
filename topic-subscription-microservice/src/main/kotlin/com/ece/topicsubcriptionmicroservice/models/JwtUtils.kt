package com.ece.topicsubcriptionmicroservice.models

import org.json.JSONObject
import java.util.*


object JwtUtils {

    fun decode(encodedString: String?): String? {
        return String(Base64.getUrlDecoder().decode(encodedString))
    }

    fun getUsernameFromJwt(token: String): String {
        val jwt = token.substring(7)
        val parts: List<String> = jwt.split(".")
        val payload = JSONObject(decode(parts[1]))
        return payload.getString("preferred_username")
    }
}
