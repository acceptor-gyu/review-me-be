package reviewme.be.util.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reviewme.be.custom.CustomErrorResponse;
import reviewme.be.util.exception.NonExistEmojiException;
import reviewme.be.util.exception.NonExistLabelException;
import reviewme.be.util.exception.NonExistOccupationException;
import reviewme.be.util.exception.NonExistScopeException;
import reviewme.be.util.exception.NotLoggedInUserException;
import reviewme.be.util.exception.NotYoursException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<CustomErrorResponse> handleNumberFormatException(NumberFormatException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "숫자만 입력 가능합니다."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({JsonParseException.class, InvalidFormatException.class})
    public ResponseEntity<CustomErrorResponse> handleJacksonInvalidFormatException(InvalidFormatException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "입력 값의 형식이 잘못됐습니다."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<CustomErrorResponse> handleTypeMismatchException(TypeMismatchException e) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "fail",
                        400,
                        "잘못된 형식입니다."));
    }


    /**
     * util exception handler
     */
    @ExceptionHandler(NonExistLabelException.class)
    public ResponseEntity<CustomErrorResponse> nonExistFeedbackLabel(NonExistLabelException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "Not Found",
                        404,
                        ex.getMessage()));
    }

    @ExceptionHandler(NonExistOccupationException.class)
    public ResponseEntity<CustomErrorResponse> nonExistOccupation(NonExistOccupationException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "Not Found",
                        404,
                        ex.getMessage()));
    }

    @ExceptionHandler(NonExistScopeException.class)
    public ResponseEntity<CustomErrorResponse> nonExistScope(NonExistScopeException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "Not Found",
                        404,
                        ex.getMessage()));
    }

    @ExceptionHandler(NonExistEmojiException.class)
    public ResponseEntity<CustomErrorResponse> nonExistEmoji(NonExistEmojiException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "Not Found",
                        404,
                        ex.getMessage()));
    }

    @ExceptionHandler(NotYoursException.class)
    public ResponseEntity<CustomErrorResponse> notYours(NotYoursException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "Bad Request",
                        400,
                        ex.getMessage()));
    }

    @ExceptionHandler(NotLoggedInUserException.class)
    public ResponseEntity<CustomErrorResponse> notLoggedInUser(NotLoggedInUserException ex) {

        return ResponseEntity
                .badRequest()
                .body(new CustomErrorResponse(
                        "Unauthorized",
                        401,
                        ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<CustomErrorResponse> sizeLimitExceededException(SizeLimitExceededException ex) {

        return ResponseEntity
            .badRequest()
            .body(new CustomErrorResponse(
                "업로드할 수 있는 pdf의 최대 크기를 초과했습니다",
                400,
                ex.getMessage()));
    }
}
