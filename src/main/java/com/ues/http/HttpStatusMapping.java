package com.ues.http;

import java.util.HashMap;
import java.util.Map;

public class HttpStatusMapping {

    public static final Map<String, HttpStatus> STATUS_CODES = new HashMap<>();
    public static final Map<String, String> RESPONSE_BODIES = new HashMap<>();

    static {
        // 2xx Success
        STATUS_CODES.put("/test200", HttpStatus.OK);
        STATUS_CODES.put("/test201", HttpStatus.CREATED);
        STATUS_CODES.put("/test202", HttpStatus.ACCEPTED);
        STATUS_CODES.put("/test203", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        STATUS_CODES.put("/test204", HttpStatus.NO_CONTENT);
        STATUS_CODES.put("/test205", HttpStatus.RESET_CONTENT);
        STATUS_CODES.put("/test206", HttpStatus.PARTIAL_CONTENT);
        STATUS_CODES.put("/test207", HttpStatus.MULTI_STATUS);
        STATUS_CODES.put("/test208", HttpStatus.ALREADY_REPORTED);
        STATUS_CODES.put("/test226", HttpStatus.IM_USED);

        // 3xx Redirection
        STATUS_CODES.put("/test301", HttpStatus.MOVED_PERMANENTLY);
        STATUS_CODES.put("/test302", HttpStatus.FOUND);
        STATUS_CODES.put("/test303", HttpStatus.SEE_OTHER);
        STATUS_CODES.put("/test304", HttpStatus.NOT_MODIFIED);
        STATUS_CODES.put("/test307", HttpStatus.TEMPORARY_REDIRECT);
        STATUS_CODES.put("/test308", HttpStatus.PERMANENT_REDIRECT);

        // 4xx Client Errors
        STATUS_CODES.put("/test400", HttpStatus.BAD_REQUEST);
        STATUS_CODES.put("/test401", HttpStatus.UNAUTHORIZED);
        STATUS_CODES.put("/test403", HttpStatus.FORBIDDEN);
        STATUS_CODES.put("/test404", HttpStatus.NOT_FOUND);
        STATUS_CODES.put("/test405", HttpStatus.METHOD_NOT_ALLOWED);
        STATUS_CODES.put("/test409", HttpStatus.CONFLICT);

        // 5xx Server Errors
        STATUS_CODES.put("/test500", HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_CODES.put("/test501", HttpStatus.NOT_IMPLEMENTED);
        STATUS_CODES.put("/test502", HttpStatus.BAD_GATEWAY);
        STATUS_CODES.put("/test503", HttpStatus.SERVICE_UNAVAILABLE);

        // Response Bodies
        RESPONSE_BODIES.put("/test200", "Request handled successfully.");
        RESPONSE_BODIES.put("/test201", "Resource created successfully.");
        RESPONSE_BODIES.put("/test202", "Request accepted but not yet processed.");
        RESPONSE_BODIES.put("/test203", "Non-authoritative information.");
        RESPONSE_BODIES.put("/test204", "No content.");
        RESPONSE_BODIES.put("/test205", "Reset content.");
        RESPONSE_BODIES.put("/test206", "Partial content returned.");
        RESPONSE_BODIES.put("/test207", "Multi-status response.");
        RESPONSE_BODIES.put("/test208", "Already reported.");
        RESPONSE_BODIES.put("/test226", "IM used.");

        RESPONSE_BODIES.put("/test301", "Resource moved permanently.");
        RESPONSE_BODIES.put("/test302", "Resource found at another location.");
        RESPONSE_BODIES.put("/test303", "See other resource.");
        RESPONSE_BODIES.put("/test304", "Resource not modified.");
        RESPONSE_BODIES.put("/test307", "Temporary redirect.");
        RESPONSE_BODIES.put("/test308", "Permanent redirect.");

        RESPONSE_BODIES.put("/test400", "Bad request.");
        RESPONSE_BODIES.put("/test401", "Unauthorized access.");
        RESPONSE_BODIES.put("/test403", "Forbidden resource.");
        RESPONSE_BODIES.put("/test404", "Resource not found.");
        RESPONSE_BODIES.put("/test405", "Method not allowed.");
        RESPONSE_BODIES.put("/test409", "Conflict with current state.");

        RESPONSE_BODIES.put("/test500", "Internal server error.");
        RESPONSE_BODIES.put("/test501", "Not implemented.");
        RESPONSE_BODIES.put("/test502", "Bad gateway.");
        RESPONSE_BODIES.put("/test503", "Service unavailable.");
    }
}
