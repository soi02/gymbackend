package com.ca.gymbackend.market.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ca.gymbackend.market.dto.MarketArticleDto;
import com.ca.gymbackend.market.dto.MarketCommentOnArticleDto;
import com.ca.gymbackend.market.dto.MarketDealedLogCheckedByBuyerDto;
import com.ca.gymbackend.market.dto.MarketDealedLogCheckedBySellerDto;
import com.ca.gymbackend.market.dto.MarketDealedLogDto;
import com.ca.gymbackend.market.dto.MarketProductInterestedLogDto;
import com.ca.gymbackend.market.dto.MarketReviewOnUserDto;
import com.ca.gymbackend.market.dto.MarketUserInfoDto;
import com.ca.gymbackend.market.mapper.MarketMapper;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class MarketService {
    
    @Autowired
    private MarketMapper marketMapper;
    
    @Autowired
    @Qualifier("fileRootPath")
    private String fileRootPath;

    public MarketUserInfoDto selectMarketUserInfo(Integer userId) {
        return marketMapper.selectMarketUserInfo(userId);
    }
    
    public void insertMarketArticleIncludesImage(MarketArticleDto marketArticleDto, MultipartFile multipartFile) throws IOException {
        
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String stringImageLink = this.saveArticleImage(multipartFile);
            marketArticleDto.setImageLink(stringImageLink);
            marketArticleDto.setImageOriginalFilename(multipartFile.getOriginalFilename());
        } else {
            marketArticleDto.setImageLink(null);
            marketArticleDto.setImageOriginalFilename(null);
        }
        
        marketMapper.insertMarketArticle(marketArticleDto);
    }
    private String saveArticleImage(MultipartFile multipartFile
    // , byte[] buffer
    ) throws IOException {
        String uuid = UUID.randomUUID().toString();
        long currentTime = System.currentTimeMillis();
        String filename = uuid + "_" + currentTime;
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        filename += ext;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");
        String todayPath = simpleDateFormat.format(new Date(currentTime));
        Path dirPath = Paths.get(fileRootPath, "marketArticleImages", todayPath);
        Files.createDirectories(dirPath);
        
        // ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
        Path filePath = dirPath.resolve(filename);
        
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Thumbnails.of(inputStream)
            .scale(1.0)
            .toFile(filePath.toFile()); 
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return "/marketArticleImages/" + todayPath + filename;
    }
    public List<Map<String, Object>> selectMarketArticle() {
        List<Map<String, Object>> mapListSelectMarketArticle = new ArrayList<>();
        List<MarketArticleDto> listMarketArticleDto = marketMapper.selectMarketArticle();
        
        System.out.println("selectMarketArticleListTest");
        System.out.println(listMarketArticleDto);
        
        if (listMarketArticleDto != null) {
        
            for (MarketArticleDto marketArticleDto : listMarketArticleDto) {
                Map<String, Object> map = new HashMap<>();
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
                map.put("marketArticleDto", marketArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketArticle.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketArticle.add(map);
            // System.out.println("selectMarketArticleTest");
            // System.out.println(map);
            
            // ▲ 필요 없는 코드
            
            listMarketArticleDto = new ArrayList<>();
            
        }
        
        // List 에는 사용 불가능한 코드 (map 구조에만 사용 가능함)
        
        return mapListSelectMarketArticle;
    }
    
    public MarketArticleDto selectSpecificMarketArticle(Integer id) {
        return marketMapper.selectSpecificMarketArticle(id);
    }
    
    public Map<String, Object> selectSpecificMarketArticleInfo(Integer id) {
        Map<String, Object> mapSelectSpecificMarketArticleInfo = new HashMap<>();
        MarketArticleDto marketArticleDto = marketMapper.selectSpecificMarketArticle(id);
        MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
        if (marketArticleDto != null) {
            marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
        } else {
            marketArticleDto = new MarketArticleDto();
        }
        mapSelectSpecificMarketArticleInfo.put("marketArticleDto", marketArticleDto);
        mapSelectSpecificMarketArticleInfo.put("marketUserInfoDto", marketUserInfoDto);
        return mapSelectSpecificMarketArticleInfo;
    }
    
    public void updateMarketArticleIncludesImage(MarketArticleDto marketArticleDto, MultipartFile multipartFile) throws IOException {
        
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String stringImageLink = this.saveArticleImage(multipartFile);
            marketArticleDto.setImageLink(stringImageLink);
            marketArticleDto.setImageOriginalFilename(multipartFile.getOriginalFilename());
        } else {
            marketArticleDto.setImageLink(null);
            marketArticleDto.setImageOriginalFilename(null);
        }
        
        marketMapper.updateMarketArticle(marketArticleDto);
    }
    public void deleteMarketArticle(Integer id) {
        marketMapper.deleteMarketArticle(id);
        marketMapper.deleteMarketCommentOnArticleWhenDeleteArticle(id);
        marketMapper.deleteMarketProductInterestedLogWhenDeleteArticle(id);
        marketMapper.deleteMarketDealedLogWhenDeleteArticle(id);
    }
    
    public void insertMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketMapper.insertMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    public List<Map<String, Object>> selectMarketCommentOnArticle(Integer articleId) {
        List<Map<String, Object>> mapListSelectMarketCommentOnArticle = new ArrayList<>();
        List<MarketCommentOnArticleDto> listMarketCommentOnArticleDto = marketMapper.selectMarketCommentOnArticle(articleId);
        
        if (listMarketCommentOnArticleDto != null) {
            
            for (MarketCommentOnArticleDto marketCommentOnArticleDto : listMarketCommentOnArticleDto) {
                Map<String, Object> map = new HashMap<>();
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketCommentOnArticleDto.getMarketUserId());
                map.put("marketCommentOnArticleDto", marketCommentOnArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketCommentOnArticle.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketCommentOnArticle.add(map);
            
            // ▲ 필요 없는 코드
            
            listMarketCommentOnArticleDto = new ArrayList<>();
            
        }
        
        return mapListSelectMarketCommentOnArticle;
    }
    public Integer selectCountMarketCommentOnArticle(Integer articleId) {
        return marketMapper.selectCountMarketCommentOnArticle(articleId);
    }
    public Map<String, Object> selectSpecificMarketCommentOnArticle(Integer id) {
        Map<String, Object> mapSelectSpecificMarketCommentOnArticle = new HashMap<>();
        MarketCommentOnArticleDto marketCommentOnArticleDto = marketMapper.selectSpecificMarketCommentOnArticle(id);
        MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
        if (marketCommentOnArticleDto != null) {
            marketUserInfoDto = marketMapper.selectMarketUserInfo(marketCommentOnArticleDto.getMarketUserId());
        } else {
            marketCommentOnArticleDto = new MarketCommentOnArticleDto();
        }
        mapSelectSpecificMarketCommentOnArticle.put("marketCommentOnArticleDto", marketCommentOnArticleDto);
        mapSelectSpecificMarketCommentOnArticle.put("marketUserInfoDto", marketUserInfoDto);
        return mapSelectSpecificMarketCommentOnArticle;
    }
    public void updateMarketCommentOnArticle(MarketCommentOnArticleDto marketCommentOnArticleDto) {
        marketMapper.updateMarketCommentOnArticle(marketCommentOnArticleDto);
    }
    public void deleteMarketCommentOnArticle(Integer id) {
        marketMapper.deleteMarketCommentOnArticle(id);
    }
    
    public void insertMarketProductInterestedLog(MarketProductInterestedLogDto marketProductInterestedLogDto) {
        marketMapper.insertMarketProductInterestedLog(marketProductInterestedLogDto);
    }
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenUserInfo(Integer marketUserId) {
        List<Map<String, Object>> mapListSelectMarketProductInterestedLogWhenUserInfo = new ArrayList<>();
        List<MarketProductInterestedLogDto> listMarketProductInterestedLogDto = marketMapper.selectMarketProductInterestedLogWhenUserInfo(marketUserId);
        
        if (listMarketProductInterestedLogDto != null) {
            
            for (MarketProductInterestedLogDto marketProductInterestedLogDto : listMarketProductInterestedLogDto) {
                Map<String, Object> map = new HashMap<>();
                MarketArticleDto marketArticleDto = marketMapper.selectSpecificMarketArticle(marketProductInterestedLogDto.getSpecificArticleId());
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
                map.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
                map.put("marketArticleDto", marketArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketProductInterestedLogWhenUserInfo.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketProductInterestedLogDto marketProductInterestedLogDto = new MarketProductInterestedLogDto();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketProductInterestedLogWhenUserInfo.add(map);
            
            // ▲ 필요 없는 코드
            
            listMarketProductInterestedLogDto = new ArrayList<>();
            
        }
        
        return mapListSelectMarketProductInterestedLogWhenUserInfo;
    }
    public Integer selectCountMarketProductInterestedLogWhenUserInfo(Integer marketUserId) {
        return marketMapper.selectCountMarketProductInterestedLogWhenUserInfo(marketUserId);
    }
    public List<Map<String, Object>> selectMarketProductInterestedLogWhenArticleInfo(Integer specificArticleId) {
        List<Map<String, Object>> mapListSelectMarketProductInterestedLogWhenArticleInfo = new ArrayList<>();
        List<MarketProductInterestedLogDto> listMarketProductInterestedLogDto = marketMapper.selectMarketProductInterestedLogWhenArticleInfo(specificArticleId);
        
        if (listMarketProductInterestedLogDto != null) {
            
            for (MarketProductInterestedLogDto marketProductInterestedLogDto : listMarketProductInterestedLogDto) {
                Map<String, Object> map = new HashMap<>();
                MarketArticleDto marketArticleDto = marketMapper.selectSpecificMarketArticle(marketProductInterestedLogDto.getSpecificArticleId());
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
                map.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
                map.put("marketArticleDto", marketArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketProductInterestedLogWhenArticleInfo.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketProductInterestedLogDto marketProductInterestedLogDto = new MarketProductInterestedLogDto();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketProductInterestedLogWhenArticleInfo.add(map);
            
            // ▲ 필요 없는 코드
            
            listMarketProductInterestedLogDto = new ArrayList<>();
            
        }
        
        return mapListSelectMarketProductInterestedLogWhenArticleInfo;
    }
    public Integer selectCountMarketProductInterestedLogWhenArticleInfo(Integer specificArticleId) {
        return marketMapper.selectCountMarketProductInterestedLogWhenArticleInfo(specificArticleId);
    }
    public Map<String, Object> selectMarketProductInterestedLogWhenUserAndArticleInfo(Integer marketUserId, Integer specificArticleId) {
        Map<String, Object> mapSelectMarketProductInterestedLogWhenUserAndArticleInfo = new HashMap<>();
        MarketProductInterestedLogDto marketProductInterestedLogDto = marketMapper.selectMarketProductInterestedLogWhenUserAndArticleInfo(marketUserId, specificArticleId);
        MarketArticleDto marketArticleDto = new MarketArticleDto();
        MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
        if (marketProductInterestedLogDto != null) {
            marketArticleDto = marketMapper.selectSpecificMarketArticle(marketProductInterestedLogDto.getSpecificArticleId());
            marketUserInfoDto = marketMapper.selectMarketUserInfo(marketProductInterestedLogDto.getMarketUserId());
        } else {
            marketProductInterestedLogDto = new MarketProductInterestedLogDto();
        }
        mapSelectMarketProductInterestedLogWhenUserAndArticleInfo.put("marketProductInterestedLogDto", marketProductInterestedLogDto);
        mapSelectMarketProductInterestedLogWhenUserAndArticleInfo.put("marketArticleDto", marketArticleDto);
        mapSelectMarketProductInterestedLogWhenUserAndArticleInfo.put("marketUserInfoDto", marketUserInfoDto);
        return mapSelectMarketProductInterestedLogWhenUserAndArticleInfo;
    }
    public void deleteMarketProductInterestedLog(Integer marketUserId, Integer specificArticleId) {
        marketMapper.deleteMarketProductInterestedLog(marketUserId, specificArticleId);
    }
    
    public void insertMarketDealedLog(MarketDealedLogDto marketDealedLogDto) {
        marketMapper.insertMarketDealedLog(marketDealedLogDto);
    }
    public void insertMarketDealedLogCheckedBySeller(MarketDealedLogCheckedBySellerDto marketDealedLogCheckedBySellerDto) {
        marketMapper.insertMarketDealedLogCheckedBySeller(marketDealedLogCheckedBySellerDto);
    }
    public void deleteMarketDealedLogCheckedBySeller(Integer specificArticleId) {
        marketMapper.deleteMarketDealedLogCheckedBySeller(specificArticleId);
    }
    public void insertMarketDealedLogCheckedByBuyer(MarketDealedLogCheckedByBuyerDto marketDealedLogDto) {
        marketMapper.insertMarketDealedLogCheckedByBuyer(marketDealedLogDto);
    }
    public void deleteMarketDealedLogCheckedByBuyer(Integer specificArticleId) {
        marketMapper.deleteMarketDealedLogCheckedByBuyer(specificArticleId);
    }
    public void deleteMarketDealedLog(MarketDealedLogDto marketDealedLogDto) {
        marketMapper.insertMarketDealedLog(marketDealedLogDto);
    } // 수정 예정 (이름만 지정했음)
    public List<Map<String, Object>> selectMarketDealedLogWhenBuyer(Integer buyerId) {
        List<Map<String, Object>> mapListSelectMarketDealedLogWhenBuyer = new ArrayList<>();
        List<MarketDealedLogDto> listMarketDealedLogDto = marketMapper.selectMarketDealedLogWhenBuyer(buyerId);
        
        if (listMarketDealedLogDto != null) {
            
            for (MarketDealedLogDto marketDealedLogDto : listMarketDealedLogDto) {
                Map<String, Object> map = new HashMap<>();
                MarketArticleDto marketArticleDto = marketMapper.selectSpecificMarketArticle(marketDealedLogDto.getSpecificArticleId());
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
                map.put("marketDealedLogDto", marketDealedLogDto);
                map.put("marketArticleDto", marketArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketDealedLogWhenBuyer.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketDealedLogDto marketDealedLogDto = new MarketDealedLogDto();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketDealedLogDto", marketDealedLogDto);
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketDealedLogWhenBuyer.add(map);
            
            // ▲ 필요 없는 코드
            
            listMarketDealedLogDto = new ArrayList<>();
            
        }
        
        return mapListSelectMarketDealedLogWhenBuyer;
    }
    public Integer selectCountMarketDealedLogWhenBuyer(Integer buyerId) {
        return marketMapper.selectCountMarketDealedLogWhenBuyer(buyerId);
    }
    public List<Map<String, Object>> selectMarketDealedLogWhenSeller(Integer sellerId) {
        List<Map<String, Object>> mapListSelectMarketDealedLogWhenSeller = new ArrayList<>();
        List<MarketDealedLogDto> listMarketDealedLogDto = marketMapper.selectMarketDealedLogWhenSeller(sellerId);
        
        if (listMarketDealedLogDto != null) {
            
            for (MarketDealedLogDto marketDealedLogDto : listMarketDealedLogDto) {
                Map<String, Object> map = new HashMap<>();
                MarketArticleDto marketArticleDto = marketMapper.selectSpecificMarketArticle(marketDealedLogDto.getSpecificArticleId());
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
                map.put("marketDealedLogDto", marketDealedLogDto);
                map.put("marketArticleDto", marketArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketDealedLogWhenSeller.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketDealedLogDto marketDealedLogDto = new MarketDealedLogDto();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketDealedLogDto", marketDealedLogDto);
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketDealedLogWhenSeller.add(map);
            
            // ▲ 필요 없는 코드
            
            listMarketDealedLogDto = new ArrayList<>();
            
        }
        
        return mapListSelectMarketDealedLogWhenSeller;
    }
    public Integer selectCountMarketDealedLogWhenSeller(Integer sellerId) {
        return marketMapper.selectCountMarketDealedLogWhenSeller(sellerId);
    }
    
    public void insertMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto) {
        marketMapper.insertMarketReviewToUser(marketReviewOnUserDto);
    }
    public MarketReviewOnUserDto selectMarketReviewToUserdd(Integer evaluatedUserId) {
        marketMapper.selectMarketReviewToUser(evaluatedUserId);
        return null;
    } // 2차 백엔드 코드 작성 시 보완 및 수정
    
    public List<Map<String, Object>> selectMarketReviewToUser(Integer evaluatedUserId) {
        List<Map<String, Object>> mapListSelectMarketReviewToUser = new ArrayList<>();
        List<MarketReviewOnUserDto> listMarketReviewOnUserDto = marketMapper.selectMarketReviewToUser(evaluatedUserId);
        
        if (listMarketReviewOnUserDto != null) {
            
            for (MarketReviewOnUserDto marketReviewOnUserDto : listMarketReviewOnUserDto) {
                Map<String, Object> map = new HashMap<>();
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketReviewOnUserDto.getEvaluatedUserId());
                map.put("marketReviewOnUserDto", marketReviewOnUserDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketReviewToUser.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketReviewOnUserDto marketReviewOnUserDto = new MarketReviewOnUserDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketReviewOnUserDto", marketReviewOnUserDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketReviewToUser.add(map);
            
            // ▲ 필요 없는 코드
            
            listMarketReviewOnUserDto = new ArrayList<>();
            
        }
        

        
        return mapListSelectMarketReviewToUser;
    }
    
    public void updateMarketReviewToUser(MarketReviewOnUserDto marketReviewOnUserDto) {
        marketMapper.updateMarketReviewToUser(marketReviewOnUserDto);
    }
    public void deleteMarketReviewToUser(Integer evaluatedUserId) {
        marketMapper.deleteMarketReviewToUser(evaluatedUserId);
    }

    // [ additional crud ]
    
    public List<Map<String, Object>> selectMarketArticleBySearchWord(String searchWord) {
        List<Map<String, Object>> mapListSelectMarketArticle = new ArrayList<>();
        List<MarketArticleDto> listMarketArticleDto = marketMapper.selectMarketArticleBySearchWord(searchWord);
        
        if (listMarketArticleDto != null) {
        
            for (MarketArticleDto marketArticleDto : listMarketArticleDto) {
                Map<String, Object> map = new HashMap<>();
                MarketUserInfoDto marketUserInfoDto = marketMapper.selectMarketUserInfo(marketArticleDto.getMarketUserId());
                map.put("marketArticleDto", marketArticleDto);
                map.put("marketUserInfoDto", marketUserInfoDto);
                mapListSelectMarketArticle.add(map);
            }
            
        } else {
            
            //
            
            // Map<String, Object> map = new HashMap<>();
            // MarketArticleDto marketArticleDto = new MarketArticleDto();
            // MarketUserInfoDto marketUserInfoDto = new MarketUserInfoDto();
            // map.put("marketArticleDto", marketArticleDto);
            // map.put("marketUserInfoDto", marketUserInfoDto);
            // mapListSelectMarketArticle.add(map);
            // System.out.println("selectMarketArticleTest");
            // System.out.println(map);
            
            // ▲ 필요 없는 코드
            
            listMarketArticleDto = new ArrayList<>();
            
        }
        
        // List 에는 사용 불가능한 코드 (map 구조에만 사용 가능함)
        
        return mapListSelectMarketArticle;
    }
    
}
