package com.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.exception.ExceptionController;
import com.security.exception.XSSServletException;
import com.security.model.ErrorResponse;
import com.security.utils.XSSValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

@Component
public class ResponseFilter extends OncePerRequestFilter {

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("#{'${skip_words}'.split(',')}")
    private List<String> skipWords;

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            RequestWrapper requestWrapper = new RequestWrapper( request, skipWords);

            String uri = requestWrapper.getRequestURI();
            System.out.println("getRequestURI : " + uri);
            String decodedURI = URLDecoder.decode(uri, "UTF-8");
            System.out.println("decodedURI : " + decodedURI);

            // XSS:  Path Variable Validation
            if (!XSSValidationUtils.isValidURL(decodedURI, skipWords)) {
                ErrorResponse errorResponse = new ErrorResponse();

                errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
                errorResponse.setMessage("XSS attack error");
                System.out.println("convertObjectToJson(errorResponse) : " + convertObjectToJson(errorResponse));
                response.getWriter().write(convertObjectToJson(errorResponse));
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }

            System.out.println("Response output: " + requestWrapper.getBody());
            if (!StringUtils.isEmpty(requestWrapper.getBody())) {

                // XSS:  Post Body data validation
                if (XSSValidationUtils.isValidURLPattern(requestWrapper.getBody(), skipWords)) {

                    filterChain.doFilter(requestWrapper, response);
                } else {
                    ErrorResponse errorResponse = new ErrorResponse();

                    errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    errorResponse.setMessage("XSS attack error");
                    response.getWriter().write(convertObjectToJson(errorResponse));
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    return;

                }
            } else {
                filterChain.doFilter(requestWrapper, response);
            }
        } catch (XSSServletException ex) {
            response.getWriter().write(ex.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }  catch (Exception ex) {
            response.getWriter().write(ex.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } finally {
            System.out.println("clean up");
        }
    }
}


