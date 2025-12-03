package com.famigo.backend.mapper;

import com.famigo.backend.entity.SpotFacility;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SpotFacilityMapper {

  /**
   * スポットIDに紐づくスポット1件の設備情報を取得するメソッド
   *
   * @param spotId スポットID
   * @return スポット1件の設備情報
   */
  SpotFacility findBySpotId(Long spotId);

}
