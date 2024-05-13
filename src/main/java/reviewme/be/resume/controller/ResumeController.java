package reviewme.be.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reviewme.be.resume.dto.request.ResumeSearchConditionParam;
import reviewme.be.resume.dto.request.UpdateResumeRequest;
import reviewme.be.resume.dto.request.UploadResumeRequest;
import reviewme.be.resume.dto.response.MyResumePageResponse;
import reviewme.be.resume.dto.response.MyResumeResponse;
import reviewme.be.resume.dto.response.ResumeDetailResponse;
import reviewme.be.resume.dto.response.ResumePageResponse;
import reviewme.be.resume.dto.response.ResumeResponse;
import reviewme.be.resume.dto.response.UploadResumeResponse;
import reviewme.be.custom.CustomResponse;
import reviewme.be.resume.service.ResumeService;
import reviewme.be.user.entity.User;

@Tag(name = "resume", description = "이력서(resume) API")
@Slf4j
@RequestMapping("/resume")
@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "이력서 업로드", description = "이력서를 업로드합니다.")
    @PostMapping(consumes = {"multipart/form-data"})
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "이력서 업로드 실패")
    })
    public ResponseEntity<CustomResponse<UploadResumeResponse>> uploadResume(
        @Parameter(description = "이력서 업로드 정보(파일 포함)", content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "file", format = "binary")))
        @ModelAttribute UploadResumeRequest uploadResumeRequest,
        @RequestAttribute("user") User user) {

        long id = resumeService.saveResume(uploadResumeRequest, user);

        return ResponseEntity
            .ok()
            .body(new CustomResponse<>(
                "success",
                200,
                "이력서 업로드에 성공했습니다.",
                UploadResumeResponse.fromSavedResumeId(id)
            ));
    }

    @Operation(summary = "이력서 목록 조회", description = "이력서 목록을 조회합니다.")
    @GetMapping
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "이력서 목록 조회 실패")
    })
    public ResponseEntity<CustomResponse<ResumePageResponse>> showResumes(
        @PageableDefault(size = 20) Pageable pageable,
        @ModelAttribute ResumeSearchConditionParam searchCondition,
        @RequestAttribute("user") User user) {

        Page<ResumeResponse> resumes = resumeService.getResumes(searchCondition, pageable, user);

        return ResponseEntity
            .ok()
            .body(new CustomResponse<>(
                "success",
                200,
                "이력서 목록 조회에 성공했습니다.",
                ResumePageResponse.fromResumePageable(resumes)
            ));
    }

    @Operation(summary = "내 이력서 목록 조회", description = "내 이력서 목록을 조회합니다.")
    @GetMapping("/my")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "내 이력서 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "내 이력서 목록 조회 실패")
    })
    public ResponseEntity<CustomResponse<MyResumePageResponse>> showMyResumes(
        @PageableDefault(size = 20) Pageable pageable,
        @RequestAttribute("user") User user) {

        Page<MyResumeResponse> myResumes = resumeService.getMyResumes(pageable, user);

        return ResponseEntity
            .ok()
            .body(new CustomResponse<>(
                "success",
                200,
                "내 이력서 목록 조회에 성공했습니다.",
                MyResumePageResponse.fromMyResumePageable(myResumes)
            ));
    }

    @Operation(summary = "이력서 상세 조회", description = "이력서 상세 내용을 조회합니다.")
    @GetMapping("/{resumeId}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 상세 내용 조회 성공"),
        @ApiResponse(responseCode = "400", description = "이력서 상세 내용 조회 실패")
    })
    public ResponseEntity<CustomResponse<ResumeDetailResponse>> showResumeDetail(
        @PathVariable long resumeId,
        @RequestAttribute("user") User user) {

        ResumeDetailResponse resumeDetail = resumeService.getResumeDetail(resumeId, user);

        return ResponseEntity
            .ok()
            .body(new CustomResponse<>(
                "success",
                200,
                "이력서 상세 내용 조회에 성공했습니다.",
                resumeDetail
            ));
    }

    @Operation(summary = "이력서 삭제", description = "이력서를 삭제합니다.")
    @DeleteMapping("/{resumeId}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "이력서 삭제 실패")
    })
    public ResponseEntity<CustomResponse<Void>> deleteResume(
        @PathVariable long resumeId,
        @RequestAttribute("user") User user) {

        resumeService.deleteResume(resumeId, user);

        return ResponseEntity
            .ok()
            .body(new CustomResponse<>(
                "success",
                200,
                "이력서 삭제에 성공했습니다."
            ));
    }

    @Operation(summary = "이력서 수정", description = "이력서를 수정합니다.")
    @PatchMapping("/{resumeId}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이력서 수정 성공"),
        @ApiResponse(responseCode = "400", description = "이력서 수정 실패")
    })
    public ResponseEntity<CustomResponse<Void>> updateResume(
        @Validated @RequestBody UpdateResumeRequest updateResumeRequest,
        @PathVariable Long resumeId,
        @RequestAttribute("user") User user) {

        resumeService.updateResume(updateResumeRequest, resumeId, user);

        return ResponseEntity
            .ok()
            .body(new CustomResponse<>(
                "success",
                200,
                "이력서 수정에 성공했습니다."
            ));
    }
}
