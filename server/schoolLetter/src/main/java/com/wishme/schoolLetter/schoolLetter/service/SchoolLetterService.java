package com.wishme.schoolLetter.schoolLetter.service;


import com.wishme.schoolLetter.asset.domain.Asset;
import com.wishme.schoolLetter.asset.repository.AssetRepository;
import com.wishme.schoolLetter.config.AES256;
import com.wishme.schoolLetter.config.RSAUtil;
import com.wishme.schoolLetter.school.domian.School;
import com.wishme.schoolLetter.school.repository.SchoolRepository;
import com.wishme.schoolLetter.schoolLetter.domain.SchoolLetter;
import com.wishme.schoolLetter.schoolLetter.dto.request.SchoolLetterWriteByUuidRequestDto;
import com.wishme.schoolLetter.schoolLetter.dto.request.SchoolLetterWriteRequestDto;
import com.wishme.schoolLetter.schoolLetter.dto.response.SchoolLetterBoardListResponseDto;
import com.wishme.schoolLetter.schoolLetter.dto.response.SchoolLetterDetailResponseDto;
import com.wishme.schoolLetter.schoolLetter.repository.SchoolLetterRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchoolLetterService {

    @Value("${key.Base64_Public_Key}")
    String publicKeyBase;

    @Value("${key.Base64_Private_Key}")
    String privateKeyBase;

    @Value("${key.AES256_Key}")
    String key;

    private final SchoolLetterRepository schoolLetterRepository;
    private final AssetRepository assetRepository;
    private final SchoolRepository schoolRepository;
    public Map<String, Object> getSchoolLetterList(Integer schoolId, Integer page) {

        Map<String, Object> resultMap = new HashMap<>();

        int pageSize = 12; // 한 페이지당 12개의 학교편지
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Order.desc("createAt")));
        Page<SchoolLetter> schoolLetterPage = schoolLetterRepository.findSchoolLettersBySchoolSeq(schoolId, pageable);
        List<SchoolLetter> schoolLetterList =  schoolLetterPage.getContent();
        List<SchoolLetterBoardListResponseDto> schoolLetterResponseDtoList = new ArrayList<>();

        if (schoolLetterList != null && schoolLetterList.size() > 0) {
            for (SchoolLetter schoolLetter : schoolLetterList) {
                schoolLetterResponseDtoList.add(
                        SchoolLetterBoardListResponseDto.builder()
                                .schoolLetterSeq(schoolLetter.getSchoolLetterSeq())
                                .assetSeq(schoolLetter.getAssetSeq().getAssetSeq())
                                .assetImg(schoolLetter.getAssetSeq().getAssetImg())
                                .build()
                );
            }
        }

        School school = schoolRepository.findBySchoolSeq(schoolId)
                .orElseThrow(IllegalArgumentException::new);
        resultMap.put("schoolLetterList", schoolLetterResponseDtoList);
        resultMap.put("schoolName", school.getSchoolName());
        resultMap.put("totalCount", schoolLetterPage.getTotalElements());
        resultMap.put("totalPage", schoolLetterPage.getTotalPages());

        return resultMap;
    }


    public SchoolLetterDetailResponseDto getSchoolLetter(Long schoolLetterId) throws Exception {

        SchoolLetter sc = schoolLetterRepository.findBySchoolLetterSeq(schoolLetterId)
                .orElseThrow(IllegalArgumentException::new);
        AES256 aes256 = new AES256(key);
        String decryptContent = aes256.decrypt(sc.getContent());


        return SchoolLetterDetailResponseDto.builder()
                .schoolLetterSeq(sc.getSchoolLetterSeq())
                .content(decryptContent)
                .schoolName(sc.getSchool().getSchoolName())
                .nickname(sc.getNickname())
                .createAt(sc.getCreateAt())
                .build();
    }


    @Transactional
    public Long writeSchoolLetter(SchoolLetterWriteRequestDto writeDto) {

        School school = schoolRepository.findBySchoolSeq(writeDto.getSchoolSeq())
                .orElseThrow(IllegalArgumentException::new);
        Asset asset = assetRepository.findByAssetSeq(writeDto.getAssetSeq())
                .orElseThrow(IllegalArgumentException::new);

        SchoolLetter schoolLetter = new SchoolLetter(writeDto, school, asset);
        schoolLetter = schoolLetterRepository.save(schoolLetter);

        return schoolLetter.getSchoolLetterSeq();
    }

    public List<Asset> getAssetList() {
        char type = 'S';
        return  assetRepository.findByType(type);
    }

    public Map<String, Object> getSchoolLetterListBuSchoolUUID(String schoolUUID, Integer page) {
        Map<String, Object> resultMap = new HashMap<>();

        int pageSize = 12; // 한 페이지당 12개의 학교편지
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Order.desc("createAt")));
        Page<SchoolLetter> schoolLetterPage = schoolLetterRepository.findSchoolLettersBySchoolUuid(schoolUUID, pageable);
        List<SchoolLetter> schoolLetterList =  schoolLetterPage.getContent();
        System.out.println(schoolLetterList.size());
        List<SchoolLetterBoardListResponseDto> schoolLetterResponseDtoList = new ArrayList<>();

        if (schoolLetterList != null && schoolLetterList.size() > 0) {
            for (SchoolLetter schoolLetter : schoolLetterList) {
                schoolLetterResponseDtoList.add(
                        SchoolLetterBoardListResponseDto.builder()
                                .schoolLetterSeq(schoolLetter.getSchoolLetterSeq())
                                .assetSeq(schoolLetter.getAssetSeq().getAssetSeq())
                                .assetImg(schoolLetter.getAssetSeq().getAssetImg())
                                .build()
                );
            }
        }

        School school = schoolRepository.findByUuid(schoolUUID)
                .orElseThrow(IllegalArgumentException::new);
        resultMap.put("schoolLetterList", schoolLetterResponseDtoList);
        resultMap.put("schoolName", school.getSchoolName());
        resultMap.put("totalCount", schoolLetterPage.getTotalElements());
        resultMap.put("totalPage", schoolLetterPage.getTotalPages());
        resultMap.put("schoolId", school.getSchoolSeq());

        return resultMap;

    }

    @Transactional
    public Long writeSchoolLetterByUuid(SchoolLetterWriteByUuidRequestDto writeDto) throws Exception {

        School school = schoolRepository.findByUuid(writeDto.getUuid())
                .orElseThrow(IllegalArgumentException::new);
        Asset asset = assetRepository.findByAssetSeq(writeDto.getAssetSeq())
                .orElseThrow(IllegalArgumentException::new);

        AES256 aes256 = new AES256(key);
        String cipherContent = aes256.encrypt(writeDto.getContent());

        SchoolLetter schoolLetter = new SchoolLetter(cipherContent, writeDto.getNickname(), school, asset);
        schoolLetter = schoolLetterRepository.save(schoolLetter);

        return schoolLetter.getSchoolLetterSeq();
    }

    @Transactional
    public Long reportLetter(Long letterSeq) {
        SchoolLetter schoolLetter = schoolLetterRepository.findBySchoolLetterSeq(letterSeq)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 편지는 존재하지 않습니다.", 1));

        schoolLetter.updateReport(true);
        schoolLetterRepository.save(schoolLetter);

        return schoolLetter.getSchoolLetterSeq();
    }
}
