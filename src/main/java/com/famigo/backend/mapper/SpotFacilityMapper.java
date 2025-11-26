package com.famigo.backend.mapper;

import com.famigo.backend.entity.SpotFacility;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpotFacilityMapper {

  /**
   * スポットIDに紐づくスポット1件の設備情報を取得するメソッド
   * @param spotId スポットID
   * @return
   */
  SpotFacility findBySpotId(Long spotId);

}
