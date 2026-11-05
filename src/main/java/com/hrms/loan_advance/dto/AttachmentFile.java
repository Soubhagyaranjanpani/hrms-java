package com.hrms.loan_advance.dto;

import org.springframework.core.io.Resource;

/** A stored attachment ready to be streamed back to the browser. Not a JSON response. */
public record AttachmentFile(Resource resource, String fileName) {
}