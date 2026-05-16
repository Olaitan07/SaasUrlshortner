package com.org.saasurlshortner.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<T> {
    private T data;
    private String message;
    private HttpStatusCode httpStatusCode;
}
