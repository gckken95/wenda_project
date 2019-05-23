package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDAO {

    String TABLE_NAME =" message ";
    String INSERT_FIELDS=" from_id,to_id,content,created_date,has_read,conversation_id";
    String SELECT_FIELDS= " id ,"+INSERT_FIELDS;


    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}"})
    List<Message> getMessageDetail(@Param("conversationId")String conversationId,
                                   @Param("offset")int offset,
                                   @Param("limit")int limit);

    @Select({"select ",INSERT_FIELDS," ,count(id) as id from ( select * from ",TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc ) tt" +
                    " group by conversation_id desc limit #{offset},#{limit}"})
    List<Message> getMessageList(@Param("userId")int userId,
                                 @Param("offset")int offset,
                                 @Param("limit")int limit);

    @Select({"select count(id) from ",TABLE_NAME," where from_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId")int userId,
                                   @Param("conversationId")String conversationId);
}
