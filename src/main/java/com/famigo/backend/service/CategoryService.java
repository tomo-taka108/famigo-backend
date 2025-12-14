package com.famigo.backend.service;

import com.famigo.backend.dto.CategoryDto;
import com.famigo.backend.mapper.CategoryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class CategoryService {

  private final CategoryMapper categoryMapper;

  /**
   * カテゴリ一覧をID昇順で取得するメソッド
   *
   * @return カテゴリ一覧
   */
  public List<CategoryDto> getAll() {
    return categoryMapper.selectAll();
  }

}
