package com.ca.gymbackend.market.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogCheckedByBuyerDto;
import com.ca.gymbackend.market.dto.MarketDealedLogCheckedBySellerDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.dto.MarketUserInfoDto;

@Mapper
public interface MarketMapper {
    
    // [ 테이블 당 기본 crud ]
    
    public MarketUserInfoDto selectMarketUserInfo(@Param("userId") Integer userId);
    
    public void insertMarketArticle(MarketArticleDto marketArticleDto);
    public List<MarketArticleDto> selectMarketArticle();
    public MarketArticleDto selectSpecificMarketArticle(@Param("id") Integer id);
    public void updateMarketArticle(MarketArticleDto marketArticleDto);
    // 장터 게시글 내용 정보 바꾸기, 장터 게시글 판매 여부 바꾸기 분할 예정
    public void deleteMarketArticle(@Param("id") Integer id);
    
    public void insertMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto);
    public List<MarketCommentOnArticleDto> selectMarketCommentOnArticle(@Param("articleId") Integer articleId);
    public Integer selectCountMarketCommentOnArticle(@Param("articleId") Integer articleId);
    public MarketCommentOnArticleDto selectSpecificMarketCommentOnArticle(@Param("id") Integer id);
    public void updateMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto);
    public void deleteMarketCommentOnArticleWhenDeleteArticle(@Param("articleId") Integer articleId);
    public void deleteMarketCommentOnArticle(@Param("id") Integer id);
    
    public void insertMarketProductInterestedLog(MarketProductInterestedLogDto marketProductInterestedLogDffto);
    public List<MarketProductInterestedLogDto> selectMarketProductInterestedLogWhenUserInfo(@Param("marketUserId") Integer marketUserId);
    public Integer selectCountMarketProductInterestedLogWhenUserInfo(@Param("marketUserId") Integer marketUserId);
    public List<MarketProductInterestedLogDto> selectMarketProductInterestedLogWhenArticleInfo(@Param("specificArticleId") Integer specificArticleId);
    public Integer selectCountMarketProductInterestedLogWhenArticleInfo(@Param("specificArticleId") Integer specificArticleId);
    public MarketProductInterestedLogDto selectMarketProductInterestedLogWhenUserAndArticleInfo(@Param("marketUserId") Integer marketUserId, @Param("specificArticleId") Integer specificArticleId);
    public void deleteMarketProductInterestedLogWhenDeleteArticle(@Param("specificArticleId") Integer specificArticleId);
    public void deleteMarketProductInterestedLog(@Param("marketUserId") Integer marketUserId, @Param("specificArticleId") Integer specificArticleId);
    
    public void insertMarketDealedLog(MarketDealedLogDto marketDealedLogDto);
    public void insertMarketDealedLogCheckedBySeller(MarketDealedLogCheckedBySellerDto marketDealedLogCheckedBySellerDto);
    public MarketDealedLogCheckedBySellerDto selectMarketDealedLogCheckedBySeller(MarketDealedLogCheckedBySellerDto marketDealedLogCheckedBySellerDto);
    public void deleteMarketDealedLogCheckedBySeller(@Param("specificArticleId") Integer specificArticleId);
    public void insertMarketDealedLogCheckedByBuyer(MarketDealedLogCheckedByBuyerDto marketDealedLogCheckedByBuyerDto);
    public MarketDealedLogCheckedByBuyerDto selectMarketDealedLogCheckedByBuyer(MarketDealedLogCheckedByBuyerDto marketDealedLogCheckedByBuyerDto);
    public void deleteMarketDealedLogCheckedByBuyer(@Param("specificArticleId") Integer specificArticleId);
    public MarketDealedLogDto selectSpecificMarketDealedLog(@Param("specificArticleId") Integer specificArticleId);
    public List<MarketDealedLogDto> selectMarketDealedLogWhenBuyer(@Param("buyerId") Integer buyerId);
    public Integer selectCountMarketDealedLogWhenBuyer(@Param("buyerId") Integer buyerId);
    public List<MarketDealedLogDto> selectMarketDealedLogWhenSeller(@Param("sellerId") Integer sellerId);
    public Integer selectCountMarketDealedLogWhenSeller(@Param("sellerId") Integer sellerId);
    public void deleteMarketDealedLogWhenDeleteArticle(@Param("specificArticleId") Integer specificArticleId);
    public void updateMarketArticleToSellEnded(@Param("id") Integer id);
    
    public void insertMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto);
    public List<MarketReviewOnUserDto> selectMarketReviewToUser(@Param("evaluatedUserId") Integer evaluatedUserId);
    public void updateMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto);
    public void deleteMarketReviewToUser(@Param("evaluatedUserId") Integer evaluatedUserId);
    
    // [ 페이지에 따른 추가 crud ]
    
    public List<MarketArticleDto> selectMarketArticleBySearchWord(@Param("searchWord") String searchWord);
    
}
