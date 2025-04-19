package com.techblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.techblog.entity.DiscountCode;
import com.techblog.entity.article.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscountCodeMapper extends BaseMapper<DiscountCode>  {
    
    @Select("SELECT * FROM tb_discount_code WHERE id = #{id}")
    DiscountCode getById(Integer id);
    
    @Select("SELECT * FROM tb_discount_code WHERE code = #{code}")
    DiscountCode getByCode(String code);
    
    @Select("SELECT * FROM tb_discount_code")
    List<DiscountCode> listAll();
    
    @Insert("INSERT INTO tb_discount_code(code, title, sub_title, rules, discount_rate, start_time, end_time, status) " +
            "VALUES(#{code}, #{title}, #{subTitle}, #{rules}, #{discountRate}, #{startTime}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscountCode discountCode);
    
    @Update("UPDATE tb_discount_code SET title = #{title}, sub_title = #{subTitle}, rules = #{rules}, " +
            "discount_rate = #{discountRate}, start_time = #{startTime}, end_time = #{endTime}, status = #{status} " +
            "WHERE id = #{id}")
    int update(DiscountCode discountCode);
    
    @Delete("DELETE FROM tb_discount_code WHERE id = #{id}")
    int deleteById(Integer id);
    
    @Update("UPDATE tb_discount_code SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
} 