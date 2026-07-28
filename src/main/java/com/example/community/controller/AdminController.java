package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.common.ResponseMessage;
import com.example.community.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController { // 해당 컨트롤러 접근 모든 요청은 SecurityFilter에서 권한 검사
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<?> loadAdminPage() {
        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.ADMIN_PAGE_LOAD.getMessage())
        );
    }

    @GetMapping("/reports")
    public ResponseEntity<?> loadReportedPostsPage(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> reportedPostsInfo = adminService.getReportedPostsInfo(page);

        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.REPORTED_POSTS_PAGE_LOAD.getMessage(), reportedPostsInfo)
        );
    }

    @GetMapping("/reports/{post_id}")
    public ResponseEntity<?> loadReportedPostPage(@PathVariable("post_id") Long postId) {
        Map<String, Object> postReportsInfo = adminService.getPostReportsInfo(postId);

        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.POST_REPORT_DETAILS_PAGE_LOAD.getMessage(), postReportsInfo)
        );
    }

    @PostMapping("/blind/{post_id}")
    public ResponseEntity<?> tryBlindPost(@PathVariable("post_id") Long postId) {
        adminService.tryBlindPostProcess(postId);

        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.POST_BLIND_SUCCESS.getMessage())
        );
    }

    @PostMapping("/reject/{post_id}")
    public ResponseEntity<?> tryRejectReportsInPost(@PathVariable("post_id") Long postId) {
        adminService.tryRejectReportsInPostProcess(postId);

        return ResponseEntity.ok(
                ResponseFormat.of(ResponseMessage.REJECT_REPORTS_SUCCESS.getMessage())
        );
    }
}