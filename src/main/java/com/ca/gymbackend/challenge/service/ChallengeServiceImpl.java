package com.ca.gymbackend.challenge.service;

import com.ca.gymbackend.challenge.dto.ChallengeCreateRequest;
import com.ca.gymbackend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl {

    private final ChallengeMapper challengeMapper;

    @Autowired
    @Qualifier("fileRootPath") // application.yml 또는 @Bean에서 설정한 경로 주입
    private String rootPath;

    /**
     * 챌린지 등록 (이미지 업로드 포함)
     */
    public void registerChallenge(ChallengeCreateRequest request, MultipartFile imageFile) throws Exception {
        // 1. 이미지 업로드
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path dirPath = Paths.get(rootPath, "challengeImages");
            Files.createDirectories(dirPath); // 폴더 없으면 생성

            Path filePath = dirPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 썸네일 경로 저장 (프론트에 줄 경로 or DB 저장용)
            request.setChallengeThumnailPath("/challengeImages/" + fileName);
        }

        // 2. 챌린지 insert
        challengeMapper.createChallenge(request);

        // 3. 생성된 챌린지 ID 가져오기
        int challengeId = challengeMapper.findLastInsertedChallengeId();

        // 4. 챌린지-키워드 연결 insert
        if (request.getChallengeKeywordIds() != null) {
            for (Integer keywordId : request.getChallengeKeywordIds()) {
                challengeMapper.createChallengeKeyword(challengeId, keywordId);
            }
        }
    }

    /**
     * 챌린지 전체 목록 조회
     */
    public List<ChallengeCreateRequest> getAllChallengeList() {
        return challengeMapper.findAllChallengeList();
    }
}
