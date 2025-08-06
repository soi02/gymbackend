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
import com.ca.gymbackend.market.service.MarketService;
import com.ca.gymbackend.security.JwtUtil;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    
    @Autowired
    private MarketService marketService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/insertMarketArticle") // ok
    public void insertMarketArticle(@RequestBody MarketArticleDto marketArticleDto) {
        marketService.insertMarketArticle(marketArticleDto);
    }
    
    @GetMapping("/selectMarketArticle") // ok
    public List<Map<String, Object>> selectMarketArticle() {
        return marketService.selectMarketArticle();
    }
    
    @GetMapping("/selectSpecificMarketArticle") // ok
    public MarketArticleDto selectSpecificMarketArticle(@RequestBody MarketArticleDto marketArticleDto) {
        return marketService.selectSpecificMarketArticle(marketArticleDto.getId());
    }
    
    @PostMapping("/updateMarketArticle") // ok
    public void updateMarketArticle(@RequestBody MarketArticleDto marketArticleDto) {
        marketService.updateMarketArticle(marketArticleDto);
    }
    
    @PostMapping("/deleteMarketArticle") // ok
    public void deleteMarketArticle(@RequestBody MarketArticleDto marketArticleDto) {
        marketService.deleteMarketArticle(marketArticleDto.getId());
    }
    
    @PostMapping("/insertMarketCommentOnArticle") // ok
    public void insertMarketCommentOnArticle(@RequestBody MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketService.insertMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    
    @GetMapping("/selectMarketCommentOnArticle") // ok
    public List<Map<String, Object>> selectMarketCommentOnArticle(@RequestParam("articleId") Integer articleId) {
        return marketService.selectMarketCommentOnArticle(articleId);
    }
    
    @PostMapping("/updateMarketCommentOnArticle") // ok
    public void updateMarketCommentOnArticle(@RequestBody MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketService.updateMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    
    @PostMapping("/deleteMarketCommentOnArticle") // ok
    public void deleteMarketCommentOnArticle(@RequestBody MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketService.deleteMarketCommentOnArticle(marketCommentOnArticleDto.getId());
    }
    
    @PostMapping("/insertMarketProductInterestedLog") // ok
    public void insertMarketProductInterestedLog(@RequestBody MarketProductInterestedLogDto marketProductInterestedLogDto) {
        marketService.insertMarketProductInterestedLog(marketProductInterestedLogDto);
    }
    
    @GetMapping("/selectMarketProductInterestedLogWhenUserInfo") // ok
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenUserInfo(@RequestParam("marketUserId") Integer marketUserId) {
        return marketService.selectMarketProductInterestedLogWhenUserInfo(marketUserId);
    }
    
    @GetMapping("/selectMarketProductInterestedLogWhenArticleInfo") // ok
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenArticleInfo(@RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectMarketProductInterestedLogWhenArticleInfo(specificArticleId);
    }
    
    @PostMapping("/insertMarketDealedLog") // ok
    public void insertMarketDealedLog(@RequestBody MarketDealedLogDto marketDealedLogDto) {
        marketService.insertMarketDealedLog(marketDealedLogDto);
    }
    
    @GetMapping("/selectMarketDealedLogWhenBuyer") // ok
    public List<Map<String, Object>> selectMarketDealedLogWhenBuyer(@RequestParam("buyerId") Integer buyerId) {
        return marketService.selectMarketDealedLogWhenBuyer(buyerId);
    }
    
    @GetMapping("/selectMarketDealedLogWhenSeller") // ok
    public List<Map<String, Object>> selectMarketDealedLogWhenSeller(@RequestParam("sellerId") Integer sellerId) {
        return marketService.selectMarketDealedLogWhenSeller(sellerId);
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
    public void deleteMarketReviewToUser(@RequestBody MarketReviewOnUserDto marketReviewOnUserDto) {
        marketService.deleteMarketReviewToUser(marketReviewOnUserDto.getEvaluatedUserId());
    }
    
    // [ additional crud ]
    
    // @GetMapping("/selectMarketArticleByTitleSearchWord")
    
    // @GetMapping("/selectMarketArticleByContentSearchWord")
    
}
