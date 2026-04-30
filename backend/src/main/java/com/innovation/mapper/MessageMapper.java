package com.innovation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.innovation.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Update("UPDATE message SET is_read = 1, read_time = NOW() WHERE receiver_id = #{receiverId} AND is_read = 0")
    int markAllRead(@Param("receiverId") Integer receiverId);
}
