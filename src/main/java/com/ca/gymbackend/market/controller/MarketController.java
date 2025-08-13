package com.ca.gymbackend.market.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.dto.MarketUserInfoDto;
import com.ca.gymbackend.market.service.MarketService;
import com.ca.gymbackend.security.JwtUtil;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    
    @Autowired
    private MarketService marketService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/selectMarketUserInfo") // ok // Front OK
    public MarketUserInfoDto selectMarketUserInfo(@RequestParam("userId") Integer userId) {
        return marketService.selectMarketUserInfo(userId);
    } 
    // temporary test code
    
    @PostMapping("/insertMarketArticle") // ok + Front OK
    public void insertMarketArticle(@RequestBody MarketArticleDto marketArticleDto) {
        marketService.insertMarketArticle(marketArticleDto);
    }
    
    @GetMapping("/selectMarketArticle") // ok + Front OK (구조 수정해야 됨)
    public List<Map<String, Object>> selectMarketArticle() {
        return marketService.selectMarketArticle();
    }
    
    @GetMapping("/selectSpecificMarketArticle") // ok + Front OK
    public MarketArticleDto selectSpecificMarketArticle(@RequestParam("id") Integer id) {
        return marketService.selectSpecificMarketArticle(id);
    }
    
    @GetMapping("/selectSpecificMarketArticleInfo") // ok
    public Map<String, Object> selectSpecificMarketArticleInfo(@RequestParam("id") Integer id) {
        return marketService.selectSpecificMarketArticleInfo(id);
    }
    
    @PostMapping("/updateMarketArticle") // ok
    public void updateMarketArticle(@RequestBody MarketArticleDto marketArticleDto) {
        marketService.updateMarketArticle(marketArticleDto);
    }
    
    @PostMapping("/deleteMarketArticle") // ok + Front OK
    public void deleteMarketArticle(@RequestParam("id") Integer id) {
        marketService.deleteMarketArticle(id);
    }
    
    @PostMapping("/insertMarketCommentOnArticle") // ok + Front OK + View Reload OK
    public void insertMarketCommentOnArticle(@RequestBody MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketService.insertMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    
    @GetMapping("/selectMarketCommentOnArticle") // ok + Front OK + View Reload OK
    public List<Map<String, Object>> selectMarketCommentOnArticle(@RequestParam("articleId") Integer articleId) {
        return marketService.selectMarketCommentOnArticle(articleId);
    }
    
    @GetMapping("/selectCountMarketCommentOnArticle") // ok
    public Integer selectCountMarketCommentOnArticle(@RequestParam("articleId") Integer articleId) {
        return marketService.selectCountMarketCommentOnArticle(articleId);
    }
    
    @PostMapping("/updateMarketCommentOnArticle") // ok
    public void updateMarketCommentOnArticle(@RequestBody MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketService.updateMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    
    @PostMapping("/deleteMarketCommentOnArticle") // ok + Front OK + View Reload OK
    public void deleteMarketCommentOnArticle(@RequestParam("id") Integer id) {
        marketService.deleteMarketCommentOnArticle(id);
    }
    
    @PostMapping("/insertMarketProductInterestedLog") // ok + Front OK
    public void insertMarketProductInterestedLog(@RequestBody MarketProductInterestedLogDto marketProductInterestedLogDto) {
        marketService.insertMarketProductInterestedLog(marketProductInterestedLogDto);
    }
    
    @GetMapping("/selectMarketProductInterestedLogWhenUserInfo") // ok + Front OK
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenUserInfo(@RequestParam("marketUserId") Integer marketUserId) {
        return marketService.selectMarketProductInterestedLogWhenUserInfo(marketUserId);
    }
    
    @GetMapping("/selectCountMarketProductInterestedLogWhenUserInfo") // ok
    public Integer selectCountMarketProductInterestedLogWhenUserInfo(@RequestParam("marketUserId") Integer marketUserId) {
        return marketService.selectCountMarketProductInterestedLogWhenUserInfo(marketUserId);
    }
    
    @GetMapping("/selectMarketProductInterestedLogWhenArticleInfo") // ok ( 탐낸 사용자 목록 비공개라 크게 필요는 없음)
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenArticleInfo(@RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectMarketProductInterestedLogWhenArticleInfo(specificArticleId);
    }
    
    @GetMapping("/selectCountMarketProductInterestedLogWhenArticleInfo") // ok
    public Integer selectCountMarketProductInterestedLogWhenArticleInfo(@RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectCountMarketProductInterestedLogWhenArticleInfo(specificArticleId);
    }
    
    @GetMapping("/selectMarketProductInterestedLogWhenUserAndArticleInfo") // ok + Front OK + View Reload OK
    public Map<String, Object> selectMarketProductInterestedLogWhenUserAndArticleInfo(@RequestParam("marketUserId") Integer marketUserId, @RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectMarketProductInterestedLogWhenUserAndArticleInfo(marketUserId, specificArticleId);
    }
    
    @PostMapping("/deleteMarketProductInterestedLog") // ok + Front OK
    public void deleteMarketProductInterestedLog(@RequestParam("marketUserId") Integer marketUserId, @RequestParam("specificArticleId") Integer specificArticleId) {
        marketService.deleteMarketProductInterestedLog(marketUserId, specificArticleId);
    }
    
    @PostMapping("/insertMarketDealedLog") // ok --- 전체적인 구조 수정 예정
    public void insertMarketDealedLog(@RequestBody MarketDealedLogDto marketDealedLogDto) {
        marketService.insertMarketDealedLog(marketDealedLogDto);
    }
    
    @GetMapping("/selectMarketDealedLogWhenBuyer") // ok + Front OK
    public List<Map<String, Object>> selectMarketDealedLogWhenBuyer(@RequestParam("buyerId") Integer buyerId) {
        return marketService.selectMarketDealedLogWhenBuyer(buyerId);
    }
    
    @GetMapping("/selectCountMarketDealedLogWhenBuyer") // ok
    public Integer selectCountMarketDealedLogWhenBuyer(@RequestParam("buyerId") Integer buyerId) {
        return marketService.selectCountMarketDealedLogWhenBuyer(buyerId);
    }
    
    @GetMapping("/selectMarketDealedLogWhenSeller") // ok + Front OK
    public List<Map<String, Object>> selectMarketDealedLogWhenSeller(@RequestParam("sellerId") Integer sellerId) {
        return marketService.selectMarketDealedLogWhenSeller(sellerId);
    }
    
    @GetMapping("/selectCountMarketDealedLogWhenSeller") // ok
    public Integer selectCountMarketDealedLogWhenSeller(@RequestParam("sellerId") Integer sellerId) {
        return marketService.selectCountMarketDealedLogWhenSeller(sellerId);
    }
    
    @PostMapping("/insertMarketReviewToUser") // ok
    public void insertMarketReviewToUser(@RequestBody MarketReviewOnUserDto marketReviewOnUserDto) {
        marketService.insertMarketReviewToUser(marketReviewOnUserDto);
    }
    
    @GetMapping("/selectMarketReviewToUser") // ok
    public List<Map<String, Object>> selectMarketReviewToUser(@RequestParam("evaluatedUserId") Integer evaluatedUserId) {
        return marketService.selectMarketReviewToUser(evaluatedUserId);
    }
    
    @PostMapping("/updateMarketReviewToUser") // ok
    public void updateMarketReviewToUser(@RequestBody MarketReviewOnUserDto marketReviewOnUserDto) {
        marketService.updateMarketReviewToUser(marketReviewOnUserDto);
    }
    
    @PostMapping("/deleteMarketReviewToUser") // ok
    public void deleteMarketReviewToUser(@RequestParam("evaluatedUserId") Integer evaluatedUserId) {
        marketService.deleteMarketReviewToUser(evaluatedUserId);
    }
    
    // [ additional crud ]
    
    // @GetMapping("/selectMarketArticleByTitleSearchWord")
    
    // @GetMapping("/selectMarketArticleByContentSearchWord")
    
    
    // 모든 코드에서 중복 코드 실행 방지용 코드 작성이 필요함 (예를 들어 view 반영 전 빠른 클릭 시 중복 실행 가능성 존재)
    
    
}
