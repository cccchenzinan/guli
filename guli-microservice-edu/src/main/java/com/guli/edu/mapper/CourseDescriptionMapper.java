package com.guli.edu.mapper;

import com.guli.edu.entity.CourseDescription;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 课程简介 Mapper 接口
 * </p>
 *
 * @author czn
 * @since 2019-07-12
 */
@Repository
public interface CourseDescriptionMapper extends BaseMapper<CourseDescription> {

}
