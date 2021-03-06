package com.example.devtoolsquartz.dao;

import com.example.devtoolsquartz.core.BaseMapper;
import com.example.devtoolsquartz.core.Tools;
import com.example.devtoolsquartz.quartz.task.QrtzJobTask;
import com.hsrg.core.DateFmtter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.util.Sqls;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

/**
 * Job task mapper
 */
@Mapper
public interface QrtzJobTaskMapper extends BaseMapper<QrtzJobTask> {

  /**
   * 统计 job name 出现的次数
   *
   * @param jobName job name
   * @return 返回出现的次数
   */
  @Select("SELECT count(*) FROM qrtz_job_task WHERE job_name = #{jobName}")
  int countJobName(@Param("jobName") String jobName);

  /**
   * 统计 trigger name 出现的次数
   *
   * @param triggerName trigger name
   * @return 返回出现的次数
   */
  @Select("SELECT count(*) FROM qrtz_job_task WHERE trigger_name = #{triggerName}")
  int countTriggerName(@Param("triggerName") String triggerName);

  /**
   * 查询调度任务的分页
   *
   * @param condition  条件
   * @param startTime  开始时间
   * @param endTime    结束时间
   * @param multiLevel 是否为多层级机构的调度(不支持)
   * @return 返回查询的列表
   */
  @Transient
  default List<QrtzJobTask> selectList(QrtzJobTask condition, Date startTime, Date endTime, boolean multiLevel) {
    final Sqls sqls = Sqls.custom();
    Tools.checkNotNull(startTime, () -> sqls.andGreaterThanOrEqualTo("create_time", fmt(startTime)));
    Tools.checkNotNull(endTime, () -> sqls.andLessThanOrEqualTo("create_time", fmt(endTime)));
    Tools.checkNotBlank(condition.getTriggerType(), s -> sqls.andLike("triggerType", s));
    Tools.checkNotBlank(condition.getJobGroup(), s -> sqls.andLike("jobGroup", s));
    Tools.checkNotBlank(condition.getJobName(), s -> sqls.andLike("jobName", s));
    Tools.checkNotBlank(condition.getTriggerGroup(), s -> sqls.andLike("triggerGroup", s));
    Tools.checkNotBlank(condition.getTriggerName(), s -> sqls.andLike("triggerName", s));
    return selectByExample(example(sqls));
  }


  static String fmt(Object time) {
    return DateFmtter.fmt(time, DateFmtter._yMd);
  }

}
