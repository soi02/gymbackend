package com.ca.gymbackend.routine.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;   


import com.ca.gymbackend.routine.dto.WorkoutLogDto;
import com.ca.gymbackend.routine.request.ActualWorkoutSaveRequest;
import com.ca.gymbackend.routine.request.MemoUpdateRequest;
import com.ca.gymbackend.routine.request.RoutineSaveRequest;
import com.ca.gymbackend.routine.response.ActualWorkoutResultResponse;
import com.ca.gymbackend.routine.response.CalendarSummaryResponse;
import com.ca.gymbackend.routine.response.RoutineDetailResponse;
import com.ca.gymbackend.routine.service.RoutineService;

@RestController
@RequestMapping("/api/routine")

public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @GetMapping("/getArticleList")
    public ResponseEntity<?> getArticleList() {
        return ResponseEntity.ok(routineService.getArticleList());
    }

    @GetMapping("/getWorkoutGuide/{id}")
    public ResponseEntity<?> getWorkoutGuide(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(routineService.getWorkoutGuide(id));
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("서버 에러: " + e.getMessage());
        }
    }

    @PostMapping("/saveRoutine")
    public ResponseEntity<?> saveRoutine(@RequestBody RoutineSaveRequest request) {
            System.out.println("🔥 saveRoutine() 진입!");
            System.out.println("📦 받은 요청: " + request);
    
        try {
            routineService.saveRoutine(request);
            return ResponseEntity.ok("루틴 저장 완료");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("루틴 저장 실패: " + e.getMessage());
        }

    }

    @GetMapping("/getRoutinesByUserId/{userId}")
    public ResponseEntity<?> getRoutinesByUserId(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(routineService.getRoutinesByUserId(userId));
    }
    
    @GetMapping("/list/{routineId}")
    public RoutineDetailResponse getRoutineDetail(@PathVariable("routineId") int routineId) {
        return routineService.getRoutineDetail(routineId);
    }

    @GetMapping("/routineSets/{routineId}")
    public RoutineDetailResponse getFullRoutineDetail(@PathVariable("routineId") int routineId) {
        return routineService.getRoutineDetail(routineId);
    }

    @PostMapping("/saveActualWorkout")
    public ResponseEntity<?> saveActualWorkout(@RequestBody ActualWorkoutSaveRequest request) {
        try {
            int workoutId = routineService.saveActualWorkout(request);
           return ResponseEntity.ok(Map.of("workoutId", workoutId)); // ✅ 이렇게 반환!
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("기록 저장 실패: " + e.getMessage());
        }
    }

    @GetMapping("/result/{workoutId}")
    public ResponseEntity<?> getWorkoutResult(@PathVariable("workoutId") int workoutId) {
        try {
            List<ActualWorkoutResultResponse> result = routineService.getWorkoutResult(workoutId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("결과 조회 실패: " + e.getMessage());
        }
    }
    
    @GetMapping("/getWorkoutByDate")
    public ResponseEntity<?> getWorkoutByDate(
        @RequestParam("userId") int userId,
        @RequestParam("date") String date
    ) {
        try {
            List<CalendarSummaryResponse> result = routineService.getWorkoutByDate(userId, date);
            return ResponseEntity.ok(result);

        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("결과 조회 실패: " + e.getMessage());
        }

    }

    @GetMapping("/getWorkoutDatesBetween")
    public ResponseEntity<?> getWorkoutDatesBetween(
        @RequestParam("userId") int userId,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate
    ) {
        System.out.println("🕒 서버 현재 시간: " + LocalDateTime.now());
        System.out.println("🕒 시스템 타임존: " + ZoneId.systemDefault());

        try {
            List<String> result = routineService.getWorkoutDatesBetween(userId, startDate, endDate);
            return ResponseEntity.ok(result);
            

        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("결과 조회 실패: " + e.getMessage());
        }
    }

    @PostMapping("/updateMemo")
    public ResponseEntity<?> saveRoutine(@RequestBody MemoUpdateRequest request) {
    
        routineService.updateMemo(request.getElementId(), request.getMemoContent());
        return ResponseEntity.ok().build();


    }


    // 사진/메모 upsert (workout_log 기준 업데이트)
    @PostMapping(
    value = "/workoutLog/{workoutId}/extras",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )    public ResponseEntity<WorkoutLogDto> upsertWorkoutLogExtras(
            @PathVariable("workoutId") int workoutId,
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
            System.out.println("memo=" + memo + ", file=" + (file != null ? file.getOriginalFilename() : "null"));

        return ResponseEntity.ok(routineService.upsertWorkoutLogExtras(workoutId, memo, file));
    }

    // 조회(업로드 후 갱신용)
    @GetMapping("/workoutLog/{workoutId}")
    public ResponseEntity<WorkoutLogDto> getWorkoutLog(@PathVariable int workoutId) {
        return ResponseEntity.ok(routineService.getWorkoutLogByWorkoutId(workoutId));
    }

    // @GetMapping("/search")
    // public ResponseEntity<List<VideoDto>> search(@RequestParam("q") String q) {
    //     String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
    //     // 3개만, 임베드 가능한 비디오만, 한국 기준/한국어 가중치
    //     String url = "https://www.googleapis.com/youtube/v3/search"
    //             + "?part=snippet&type=video&maxResults=3&videoEmbeddable=true"
    //             + "&safeSearch=strict&regionCode=KR&relevanceLanguage=ko"
    //             + "&q=" + encoded
    //             + "&key=" + apiKey;

    //     SearchResponse body = rest.getForObject(url, SearchResponse.class);
    //     if (body == null || body.items == null) return ResponseEntity.ok(List.of());

    //     List<VideoDto> result = body.items.stream()
    //             .filter(i -> i.id != null && i.id.videoId != null && i.snippet != null)
    //             .map(i -> new VideoDto(
    //                     i.id.videoId,
    //                     i.snippet.title,
    //                     i.snippet.thumbnails != null && i.snippet.thumbnails.high != null
    //                             ? i.snippet.thumbnails.high.url
    //                             : (i.snippet.thumbnails != null && i.snippet.thumbnails.medium != null
    //                                 ? i.snippet.thumbnails.medium.url
    //                                 : null)
    //             ))
    //             .toList();

    //     return ResponseEntity.ok(result);
    // }



}
