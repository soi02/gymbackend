package com.ca.gymbackend.market.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    
    // @GetMapping("/selectMarketArticle")
    
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
    
    // @GetMapping("/selectMarketCommentOnArticle")
    
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
    
    // @GetMapping("/selectMarketProductInterestedLogWhenUserInfo")

    // @GetMapping("/selectMarketProductInterestedLogWhenArticleInfo")
    
    @PostMapping("/insertMarketDealedLog") // ok
    public void insertMarketDealedLog(@RequestBody MarketDealedLogDto marketDealedLogDto) {
        marketService.insertMarketDealedLog(marketDealedLogDto);
    }
    
    // @GetMapping("/selectMarketDealedLogWhenBuyer")
    
    // @GetMapping("/selectMarketDealedLogWhenSeller")
    
    @PostMapping("/insertMarketReviewToUser") // ok
    public void insertMarketReviewToUser(@RequestBody MarketReviewOnUserDto marketReviewOnUserDto) {
        marketService.insertMarketReviewToUser(marketReviewOnUserDto);
    }
    
    // @GetMapping("/selectMarketReviewToUser")
    
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
