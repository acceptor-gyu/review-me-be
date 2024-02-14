package reviewme.be.question.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reviewme.be.question.dto.request.*;
import reviewme.be.question.dto.response.CommentOfQuestionPageResponse;
import reviewme.be.question.dto.response.QuestionPageResponse;
import reviewme.be.custom.CustomResponse;
import reviewme.be.question.service.QuestionService;
import reviewme.be.user.entity.User;
import reviewme.be.util.dto.response.LabelPageResponse;
import reviewme.be.util.dto.response.LabelResponse;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "question", description = "예상 질문(question) API")
@RequestMapping("/resume/{resumeId}/question")
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "예상 질문 추가", description = "예상 질문을 추가합니다.")
    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 추가 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 추가 실패")
    })
    public ResponseEntity<CustomResponse<Void>> postQuestions(
            @Validated @RequestBody CreateQuestionRequest createQuestionRequest,
            @PathVariable long resumeId,
            @RequestAttribute("user") User user) {

        questionService.saveQuestion(createQuestionRequest, resumeId, user);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 추가에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문에 대댓글 추가", description = "예상 질문에 대댓글을 추가합니다.")
    @PostMapping("/{questionId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문에 대댓글 추가 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문에 대댓글 추가 실패")
    })
    public ResponseEntity<CustomResponse<Void>> postQuestionComment(
            @Validated @RequestBody CreateQuestionCommentRequest createQuestionCommentRequest,
            @PathVariable long resumeId,
            @PathVariable long questionId,
            @RequestAttribute("user") User user) {

        questionService.saveQuestionComment(createQuestionCommentRequest, user, resumeId, questionId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 대댓글 추가에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문 목록 조회", description = "예상 질문 목록을 조회합니다.")
    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 목록 조회 실패")
    })
    public ResponseEntity<CustomResponse<QuestionPageResponse>> showQuestions(@PathVariable long resumeId, @PageableDefault(size=20) Pageable pageable, @RequestParam long resumePage) {

        // TODO: 본인의 resume인지 다른 사람의 resume인지에 따라 다른 데이터 응답 처리

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 목록 조회에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문에 달린 예상 질문(댓글) 목록 조회", description = "예상 질문에 달린 댓글 목록을 조회합니다.")
    @GetMapping("/{questionId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 댓글 목록 조회 실패")
    })
    public ResponseEntity<CustomResponse<CommentOfQuestionPageResponse>> showCommentsOfQuestions(@PathVariable long resumeId, @PathVariable long questionId, @PageableDefault(size=20) Pageable pageable) {

        // TODO: 본인의 resume인지 다른 사람의 resume인지에 따라 다른 데이터 응답 처리

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 댓글 조회에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문 삭제", description = "예상 질문을 삭제합니다.")
    @DeleteMapping("/{questionId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 삭제 실패")
    })
    public ResponseEntity<CustomResponse<Void>> deleteQuestions(
            @PathVariable long resumeId,
            @PathVariable long questionId,
            @RequestAttribute("user") User user) {

        questionService.deleteQuestion(resumeId, questionId, user);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 삭제에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문 내용 수정", description = "예상 질문 내용을 수정합니다.")
    @PatchMapping("/{questionId}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 수정 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 수정 실패")
    })
    public ResponseEntity<CustomResponse<Void>> updateQuestionContent(
            @Validated @RequestBody UpdateQuestionContentRequest updateQuestionContentRequest,
            @PathVariable long resumeId,
            @PathVariable long questionId,
            @RequestAttribute("user") User user) {

        questionService.updateQuestionContent(updateQuestionContentRequest, resumeId, questionId, user);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 수정에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문 체크 상태 수정", description = "본인의 이력서에 대한 예상 질문 내용에 체크 상태를 수정합니다.")
    @PatchMapping("/{questionId}/check")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 체크 상태 수정 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 체크 상태 수정 실패")
    })
    public ResponseEntity<CustomResponse<Void>> updateQuestionCheck(
            @Validated @RequestBody UpdateQuestionCheckRequest updateQuestionCheckRequest,
            @PathVariable long resumeId,
            @PathVariable long questionId,
            @RequestAttribute("user") User user) {

        questionService.updateQuestionCheck(updateQuestionCheckRequest, resumeId, questionId, user);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 체크 상태 수정에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문 북마크 상태 수정", description = "본인의 이력서에 대한 예상 질문 내용에 북마크 상태를 수정합니다.")
    @PatchMapping("/{questionId}/bookmark")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 북마크 상태 수정 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 북마크 상태 수정 실패")
    })
    public ResponseEntity<CustomResponse<Void>> updateQuestionBookmark(
            @Validated @RequestBody UpdateQuestionBookmarkRequest updateQuestionBookmarkRequest,
            @PathVariable long resumeId,
            @PathVariable long questionId,
            @RequestAttribute("user") User user) {

        questionService.updateQuestionBookmark(updateQuestionBookmarkRequest, resumeId, questionId, user);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 북마크 상태 수정에 성공했습니다."
                ));
    }

    @Operation(summary = "예상 질문 라벨 목록 조회", description = "예상 질문 라벨 목록을 조회합니다.")
    @GetMapping("/label")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 라벨 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 라벨 목록 조회 실패")
    })
    public ResponseEntity<CustomResponse<LabelPageResponse>> showQuestionLabels(
            @PathVariable long resumeId) {

        List<LabelResponse> questionLabelsResponse = questionService.findQuestionLabels(resumeId)
                .stream()
                .map(LabelResponse::fromLabel)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 라벨 목록 조회에 성공했습니다.",
                        LabelPageResponse.builder()
                                .labels(questionLabelsResponse)
                                .build()
                ));
    }

    @Operation(summary = "예상 질문 이모지 수정", description = "예상 질문에 표시할 이모지를 수정합니다.")
    @PatchMapping("/{questionId}/emoji")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예상 질문 이모지 상태 수정 성공"),
            @ApiResponse(responseCode = "400", description = "예상 질문 이모지 상태 수정 실패")
    })
    public ResponseEntity<CustomResponse<Void>> updateQuestionEmoji(@Validated @RequestBody UpdateQuestionEmojiRequest updateQuestionEmojiRequest, @PathVariable long resumeId, @PathVariable long questionId) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "예상 질문 이모지 수정에 성공했습니다."
                ));
    }
}
