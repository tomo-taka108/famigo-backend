package com.famigo.backend.service;

import com.famigo.backend.entity.SpotFacility;
import com.famigo.backend.mapper.SpotFacilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotFacilityService {

  private final SpotFacilityMapper spotFacilityMapper;

  /**
   * スポットIDに紐づくスポット1件の設備情報を取得するメソッド
   *
   * @param spotId
   * @return スポット1件の設備情報
   */
  public SpotFacility getSpotFacilityBySpotId(Long spotId) {
    return spotFacilityMapper.findBySpotId(spotId);
  }

}
