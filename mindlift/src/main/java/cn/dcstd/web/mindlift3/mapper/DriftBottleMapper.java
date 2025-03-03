package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.DriftBottleLibDO;
import cn.dcstd.web.mindlift3.entity.DriftBottleRepDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DriftBottleMapper {

    @Select("SELECT * FROM drift_bottle_lib ORDER BY RAND() LIMIT 6")
    List<DriftBottleLibDO> getRandomDriftBottleList();

    @Select("SELECT * FROM drift_bottle_lib WHERE userId = #{id}")
    List<DriftBottleLibDO> getMyDriftBottleList(@Param("id") int id);

    @Insert("INSERT INTO drift_bottle_rep(driftBottleId, content, userId, createTime) VALUES(#{driftId}, #{content}, #{id}, NOW())")
    void replyDriftBottle(@Param("driftId") int driftId, @Param("content") String content, @Param("id") int id);

    @Select("SELECT * FROM drift_bottle_rep WHERE userId = #{id}")
    List<DriftBottleRepDO> getMyReplyDriftBottle(@Param("id") int id);

    @Select("SELECT * FROM drift_bottle_lib WHERE id = #{driftBottleId}")
    DriftBottleLibDO getDriftBottleById(@Param("driftBottleId") int driftBottleId);

    @Select("SELECT * FROM drift_bottle_rep WHERE driftBottleId = #{id}")
    List<DriftBottleRepDO> getReplyMyDriftBottle(@Param("id") int id);
}
