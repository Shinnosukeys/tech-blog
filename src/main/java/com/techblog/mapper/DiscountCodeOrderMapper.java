package com.techblog.mapper;

import com.techblog.entity.DiscountCodeOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscountCodeOrderMapper {

    @Select("SELECT * FROM tb_discount_code_order WHERE id = #{id}")
    DiscountCodeOrder getById(Integer id);

    @Select("SELECT * FROM tb_discount_code_order WHERE user_id = #{userId}")
    List<DiscountCodeOrder> getByUserId(Integer userId);

    @Select("SELECT * FROM tb_discount_code_order WHERE discount_code_id = #{discountCodeId}")
    List<DiscountCodeOrder> getByDiscountCodeId(Integer discountCodeId);

    @Insert("INSERT INTO tb_discount_code_order(user_id, discount_code_id, status) " +
            "VALUES(#{userId}, #{discountCodeId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiscountCodeOrder discountCodeOrder);

    @Update("UPDATE tb_discount_code_order SET status = #{status}, use_time = #{useTime} " +
            "WHERE id = #{id}")
    int update(DiscountCodeOrder discountCodeOrder);

    @Select("SELECT COUNT(*) FROM tb_discount_code_order " +
            "WHERE user_id = #{userId} AND discount_code_id = #{discountCodeId}")
    int countByUserIdAndDiscountCodeId(@Param("userId") Integer userId,
                                       @Param("discountCodeId") Integer discountCodeId);
}