package cn.dcstd.web.mindlift3.mapper;

import cn.dcstd.web.mindlift3.entity.TrainHistoryDO;
import cn.dcstd.web.mindlift3.entity.TrainLibDO;
import cn.dcstd.web.mindlift3.entity.TrainLogicLibDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author NaNo1c
 */
@Mapper
public interface TrainMapper {

    @Select("select * from train_lib")
    List<TrainLibDO> getTrainList();

    @Select("select * from train_logic_lib where mainId = #{id} and isFirst = 1")
    TrainLogicLibDO getFirstTrainDetail(@Param("id") int id);

    @Select("select * from train_logic_lib where mainId = #{id} and id = #{nextId}")
    TrainLogicLibDO getTrainDetail(@Param("id") int id,@Param("nextId") int nextId);

    @Update("update train_lib set count = count + 1 where id = #{id}")
    void addCount(int id);

    @Insert("insert into train_history (userId, trainId, createTime) values (#{uid}, #{tid}, NOW())")
    void addHistory(@Param("uid") int uid, @Param("tid") int tid);

    @Update("update train_history set nextId = #{nextIdInt} where trainId = #{id}")
    void updateHistory(@Param("id") int id, @Param("nextIdInt") int nextIdInt);

    @Select("select id from train_history where userId = #{uid} and trainId = #{tid} ORDER BY id DESC LIMIT 1")
    int getHistoryId(@Param("uid") int uid, @Param("tid") int tid);

    @Select("select * from train_history where userId = #{uid}")
    List<TrainHistoryDO> getTrainHistoryListByUid(int uid);
}
