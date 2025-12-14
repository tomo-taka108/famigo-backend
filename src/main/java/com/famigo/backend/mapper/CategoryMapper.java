package com.famigo.backend.mapper;

import com.famigo.backend.dto.CategoryDto;
import com.famigo.backend.entity.SpotFacility;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

  /**
   * カテゴリ一覧ID昇順で取得するメソッド
   *
   * @return カテゴリ一覧
   */
  List<CategoryDto> selectAll();

}
