package dev.bugtracker.demo.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    // handleResourceNotFoundException : triggers when there is no resource with the specified ID in DB
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {

        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND, "Resource Not Found", details);

        return ResponseEntityBuilder.build(err);
    }


    // handleHttpMessageNotReadable : triggers when the request body is invalid
    @Override
    protected @NotNull ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                           @NotNull HttpHeaders headers,
                                                                           @NotNull HttpStatus status,
                                                                           @NotNull WebRequest request) {

        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Malformed JSON request", details);

        return ResponseEntityBuilder.build(err);
    }


    // handleMethodArgumentNotValid : triggers when @Valid fails
    @Override
    protected @NotNull ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                           @NotNull HttpHeaders headers,
                                                                           @NotNull HttpStatus status,
                                                                           @NotNull WebRequest request) {

        List<String> details;
        details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getObjectName() + " : " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Validation Errors", details);

        return ResponseEntityBuilder.build(err);
    }


    // handleMethodArgumentTypeMismatch : triggers when a parameter's type does not match
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Mismatch Type", details);

        return ResponseEntityBuilder.build(err);
    }


    // handleConstraintViolationException : triggers when @Validated fails
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(Exception ex, WebRequest request) {

        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Constraint Violation", details);

        return ResponseEntityBuilder.build(err);
    }


    // handleMissingServletRequestParameter : triggers when there are missing parameters
    @Override
    protected @NotNull ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                                   @NotNull HttpHeaders headers,
                                                                                   @NotNull HttpStatus status,
                                                                                   @NotNull WebRequest request) {

        List<String> details = new ArrayList<>();
        details.add(ex.getParameterName() + " parameter is missing");

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Missing Parameters", details);

        return ResponseEntityBuilder.build(err);
    }


    // handleHttpMediaTypeNotSupported : triggers when the JSON is invalid
    @Override
    protected @NotNull ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                              @NotNull HttpHeaders headers,
                                                                              @NotNull HttpStatus status,
                                                                              @NotNull WebRequest request) {

        List<String> details = new ArrayList<>();


        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        details.add(builder.toString());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Invalid JSON", details);

        return ResponseEntityBuilder.build(err);

    }


    // handleNoHandlerFoundException : triggers when the handler method is invalid
    @Override
    protected @NotNull ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                            @NotNull HttpHeaders headers,
                                                                            @NotNull HttpStatus status,
                                                                            @NotNull WebRequest request) {

        List<String> details = new ArrayList<>();
        details.add(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Method Not Found", details);

        return ResponseEntityBuilder.build(err);

    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex) {

        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, "Error occurred", details);

        return ResponseEntityBuilder.build(err);
    }
}
