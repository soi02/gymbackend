package com.ca.gymbackend.market.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogCheckedByBuyerDto;
import com.ca.gymbackend.market.dto.MarketDealedLogCheckedBySellerDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.dto.MarketUserInfoDto;
import com.ca.gymbackend.market.service.MarketService;
import com.ca.gymbackend.security.JwtUtil;
import com.ca.gymbackend.portal.dto.UserDto;

@RestController
@RequestMapping("/api/market")
public class MarketController {
    
    @Autowired
    private MarketService marketService;
    // public Market
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/selectMarketUserInfo") // ok // Front OK
    public UserDto selectMarketUserInfo(@RequestParam("userId") Integer userId) {
        return marketService.selectMarketUserInfo(userId);
    } 
    
    
    // temporary test code
    
    // @PostMapping("/insertMarketArticle") // ok + Front OK
    // public void insertMarketArticle(@ModelAttribute MarketArticleDto marketArticleDto, @RequestParam(value = "imageFile", required = false) MultipartFile multipartFile) {
    //     System.out.println("marketArticleDto" + marketArticleDto);
    //     System.out.println("multipartFile" + multipartFile);
    //     marketService.insertMarketArticle(marketArticleDto);
    // }
    
    @PostMapping("/insertMarketArticle") // ok + Front OK
    public void insertMarketArticle(@RequestParam(value = "imageLink", required = false) MultipartFile multipartFile,
    @RequestParam("marketUserId") Integer marketUserId, @RequestParam("title") String title,
    @RequestParam("productCost") Integer productCost, @RequestParam("content") String content) throws IOException {
        MarketArticleDto marketArticleDto = new MarketArticleDto();
        marketArticleDto.setMarketUserId(marketUserId);
        marketArticleDto.setTitle(title);
        marketArticleDto.setProductCost(productCost);
        marketArticleDto.setContent(content);
        marketService.insertMarketArticleIncludesImage(marketArticleDto, multipartFile);
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
    public void updateMarketArticle(@RequestParam(value = "imageLink", required = false) MultipartFile multipartFile,
    @RequestParam("id") Integer id,@RequestParam("marketUserId") Integer marketUserId, @RequestParam("title") String title, 
    @RequestParam("productCost") Integer productCost, @RequestParam("content") String content) throws IOException {
        MarketArticleDto marketArticleDto = new MarketArticleDto();
        marketArticleDto.setId(id);
        marketArticleDto.setMarketUserId(marketUserId);
        marketArticleDto.setTitle(title);
        marketArticleDto.setProductCost(productCost);
        marketArticleDto.setContent(content);
        marketService.updateMarketArticleIncludesImage(marketArticleDto, multipartFile);
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
    
    @GetMapping("/selectSpecificMarketCommentOnArticle") // ok
    public Map<String, Object> selectSpecificMarketCommentOnArticle(@RequestParam("id") Integer id) {
        return marketService.selectSpecificMarketCommentOnArticle(id);
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
    
    @PostMapping("/insertMarketDealedLogCheckedBySeller") // ok
    public void insertMarketDealedLogCheckedBySeller(@RequestBody MarketDealedLogCheckedBySellerDto marketDealedLogCheckedBySellerDto) {
        marketService.insertMarketDealedLogCheckedBySeller(marketDealedLogCheckedBySellerDto);
    }
    @GetMapping("/selectSpecificMarketDealedLogCheckedBySeller") // ok
    public MarketDealedLogCheckedBySellerDto selectSpecificMarketDealedLogCheckedBySeller(@RequestParam("sellerId") Integer sellerId, @RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectSpecificMarketDealedLogCheckedBySeller(sellerId, specificArticleId);
    }
    @PostMapping("/deleteMarketDealedLogCheckedBySeller") // ok --- 전체적인 구조 수정 예정 (변경 못 하니까 신중하게 선택하라고 우선은 할 생각)
    public void deleteMarketDealedLogCheckedBySeller(@RequestParam("specificArticleId") Integer specificArticleId) {
        marketService.deleteMarketDealedLogCheckedBySeller(specificArticleId);
    }
    
    @PostMapping("/insertMarketDealedLogCheckedByBuyer") // ok
    public void insertMarketDealedLogCheckedByBuyer(@RequestBody MarketDealedLogCheckedByBuyerDto marketDealedLogCheckedByBuyerDto) {
        marketService.insertMarketDealedLogCheckedByBuyer(marketDealedLogCheckedByBuyerDto);
    }
    @GetMapping("/selectSpecificMarketDealedLogCheckedByBuyer") // ok
    public MarketDealedLogCheckedByBuyerDto selectSpecificMarketDealedLogCheckedByBuyer(@RequestParam("buyerId") Integer buyerId, @RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectSpecificMarketDealedLogCheckedByBuyer(buyerId, specificArticleId);
    }
    @PostMapping("/deleteMarketDealedLogCheckedByBuyer") // ok --- 전체적인 구조 수정 예정 (변경 못 하니까 신중하게 선택하라고 우선은 할 생각)
    public void deleteMarketDealedLogCheckedByBuyer(@RequestParam("specificArticleId") Integer specificArticleId) {
        marketService.deleteMarketDealedLogCheckedByBuyer(specificArticleId);
    }
    
    @GetMapping("/selectSpecificMarketDealedLog")
    public MarketDealedLogDto selectSpecificMarketDealedLog(@RequestParam("specificArticleId") Integer specificArticleId) {
        return marketService.selectSpecificMarketDealedLog(specificArticleId);
    }
    
    @PostMapping("/updateMarketArticleToDealerVerified")
    public void updateMarketArticleToDealerVerified(@RequestParam("id") Integer id) {
        marketService.updateMarketArticleToDealerVerified(id);
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
    
    @GetMapping("/selectMarketArticleWhenSeller") // ok + Front OK (구조 수정해야 됨)
    public List<Map<String, Object>> selectMarketArticleWhenSeller(@RequestParam("marketUserId") Integer marketUserId) {
        return marketService.selectMarketArticleWhenSeller(marketUserId);
    }
    
    @GetMapping("/selectCountMarketTotalLogWhenSeller") // ok
    public Integer selectCountMarketTotalLogWhenSeller(@RequestParam("marketUserId") Integer marketUserId) {
        return marketService.selectCountMarketTotalLogWhenSeller(marketUserId);
    }
    
    @GetMapping("/selectCountMarketUndealedLogWhenSeller") // ok
    public Integer selectCountMarketUndealedLogWhenSeller(@RequestParam("marketUserId") Integer marketUserId) {
        return marketService.selectCountMarketUndealedLogWhenSeller(marketUserId);
    }
    
    @GetMapping("/selectCountMarketDealedLogWhenSeller") // ok
    public Integer selectCountMarketDealedLogWhenSeller(@RequestParam("sellerId") Integer sellerId) {
        return marketService.selectCountMarketDealedLogWhenSeller(sellerId);
    }
    
    @PostMapping("/updateMarketArticleToSellEnded")
    public void updateMarketArticleToSellEnded(@RequestParam("id") Integer id) {
        marketService.updateMarketArticleToSellEnded(id);
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
    
    @GetMapping("/selectMarketArticleBySearchWord")
    public List<Map<String, Object>> selectMarketArticleBySearchWord(@RequestParam("searchWord") String searchWord) {
        return marketService.selectMarketArticleBySearchWord(searchWord);
    }
    
    
}
